package edu.toronto.cs.silva

import java.util.Date

import akka.actor.Actor
import akka.event.{Logging, LoggingAdapter}
import edu.toronto.cs.silva.Vcf.HarmfulnessEntry
import spray.http.HttpHeaders.RawHeader
import spray.http.MultipartFormData
import spray.http.StatusCodes._
import spray.json.{JsArray, JsNumber, JsString}
import spray.routing.{HttpService, StandardRoute}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.{implicitConversions, postfixOps}
import scala.util.Random

/**
 *
 */
private[silva] class SilvaActor extends Actor with SilvaService {
  setLogging(Logging(context.system, this))

  def actorRefFactory = context

  def receive = runRoute(routes)
}

private trait SilvaService extends HttpService {
  private var logging: Option[LoggingAdapter] = None

  protected def setLogging(l: LoggingAdapter) = logging = Option(l)

  /** Holds the id of the request with the results. */
  private var resultsHolder = Map[String, Either[String, Iterable[HarmfulnessEntry]]]()
  /** Holds the id of a request mapped to the expiry time. */
  private var expiry = Map[String, Long]()
  /** How many VCFs are currently being processed. */
  private var processingCounter = 0

  val routes = {
    // fixme. can't be all
    respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
      options {
            complete("")
      } ~
      get {
        path("results" / Rest) { id ⇒
          resultsRetrieval(id)
        }
      } ~ path("vcf-upload") {
        post {
          entity(as[MultipartFormData]) { vcfUploadLogic }
        }
      }
    }
  }

  /** All processing steps applied to an uploaded VCF file. */
  private def vcfUploadLogic(data: MultipartFormData): StandardRoute = {
    cleanExpired()
    if (processingCounter >= ServerSettings.maxSimultaneousProcesses) {
      logging foreach { log ⇒ log.warning(Messages.logWarningMaxProcess) }
      return complete(ServiceUnavailable, Messages.maxProcesses)
    }

    val time = new Date().getTime
    val id = "ID" + time + Random.nextInt
    expiry += id → (time + ServerSettings.expireIn)

    asyncProcessing(id, data)
    complete(id)
  }

  private def asyncProcessing(id: String, data: MultipartFormData) = Future {
    processingCounter += 1

    val vcf = optionToError(getVcf(data), Messages.noVCF)
    val harmfulness: Either[String, Iterable[HarmfulnessEntry]] = vcf.right map { vcf ⇒
      optionToError(Vcf.getHarmfulness(vcf), Messages.couldNotFindHarmfulness)
    } joinRight;
    resultsHolder += id → harmfulness

    processingCounter -= 1
  }

  private def cleanExpired() = {
    val time = new Date().getTime
    val expired = expiry.filter(_._2 < time)

    resultsHolder = resultsHolder.filterNot(e ⇒ expired contains e._1)
    // in case the results have not been computed yet, do not expire
    expiry = expiry filterKeys (id ⇒ !{expired contains id} || {resultsHolder contains id})

    if (expired.nonEmpty) logging foreach { l ⇒ l.info(Messages.logHasExpired) }
  }

  private def resultsRetrieval(id: String): StandardRoute = {
    resultsHolder.get(id) match {
      case Some(harmfulness) ⇒
        harmfulness.right foreach { _ ⇒
          resultsHolder -= id
          expiry -= id
        }
        getAvailableResult(harmfulness)
      case _ ⇒
        if (expiry contains id)
          complete(NoContent)
        else
          complete(BadRequest, Messages.idDoesNotExist)
    }
  }

  private def getAvailableResult(result: Either[String, Iterable[HarmfulnessEntry]]) = {
    val json: Either[String, List[JsArray]] = result.right map { _ map entryToJson toList }
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