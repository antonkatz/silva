package edu.toronto.cs.silva

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern.ask

/**
 *
 */
object Boot extends App
{
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[SilvaActor], "silva")

  implicit val timeout = Timeout(5.seconds)
  val binding = Http.Bind(service, "localhost", port = 8080)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? binding
}
