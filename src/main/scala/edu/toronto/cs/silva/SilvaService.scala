package edu.toronto.cs.silva

import akka.actor.Actor
import spray.routing.{HttpService, RequestContext}

/**
 *
 */
private[silva] class SilvaActor extends Actor with SilvaService
{
  def actorRefFactory = context

  def receive = runRoute(routes)
}

private trait SilvaService extends HttpService {
  val routes = {
    get {
      path("") {
        complete("Test")
      }
    }
  }
}
