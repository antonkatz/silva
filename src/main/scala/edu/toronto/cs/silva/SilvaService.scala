package edu.toronto.cs.silva

import akka.actor.Actor
import edu.toronto.cs.silva.Vcf.HarmfulnessEntry
import spray.http.HttpHeaders.RawHeader
import spray.http.MultipartFormData
import spray.http.StatusCodes._
import spray.json.{JsArray, JsNumber, JsString}
import spray.routing.{HttpService, StandardRoute}

import scala.language.{implicitConversions, postfixOps}

/**
 *
 */
private[silva] class SilvaActor extends Actor with SilvaService {
  def actorRefFactory = context

  def receive = runRoute(routes)
}

private trait SilvaService extends HttpService {
  val routes = {
    get {
      path("") {
        complete("Test")
      }
    } ~
        respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
          path("vcf-upload") {
            options {
              complete("")
            } ~
                get {
                  complete("")
                } ~
                post {
                  entity(as[MultipartFormData]) { vcfUploadLogic }
                }
          }
        }
  }

  /** All processing steps applied to an uploaded VCF file. */
  private def vcfUploadLogic(data: MultipartFormData): StandardRoute = {
    /*fixme add counter. max simultaneous processes 3. */
    val vcf = optionToError(getVcf(data), ErrorMessages.noVCF)
    val harmfulness: Either[String, Iterable[HarmfulnessEntry]] = vcf.right map { vcf ⇒
      optionToError(Vcf.getHarmfulness(vcf), ErrorMessages.couldNotFindHarmfulness)
    } joinRight
    val json: Either[String, List[JsArray]] = harmfulness.right map { _ map entryToJson toList }
    json match {
      case Right(j) ⇒ complete(JsArray(j: _*) toString)
      case Left(e) ⇒ complete(BadRequest, e)
    }
  }

  /** Extracts VCF file from the multipart form data. */
  private def getVcf(data: MultipartFormData): Option[String] = data.get("file") map { _.entity asString }

  private[silva] def entryToJson(e: HarmfulnessEntry): JsArray = {
    JsArray(JsString(e._1 toString), JsNumber(e._2), JsString(e._3 toString), JsString(e._4 toString), JsNumber(e._5))
  }

  private def optionToError[T](x: Option[T], e: String): Either[String, T] = x match {
    case Some(v) ⇒ Right(v)
    case _ ⇒ Left(e)
  }

}