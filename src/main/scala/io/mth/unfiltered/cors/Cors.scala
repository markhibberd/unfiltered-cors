package io.mth.unfiltered.cors

import unfiltered.request.{Connection => _, _}
import unfiltered.response._
import unfiltered.kit._
import unfiltered.Cycle

/**
 * Kit for implementing CORS as per http://www.w3.org/TR/cors/.
 * {{{
 *    val cors = Cors(
 *      CorsConfig.basic("http://localhost")
 *    )
 *
 *    def intent = cors {
 *      case GET(p) => ....
 *      case POST(p) => ....
 *    }
 * }}}
 */
case class Cors(config: CorsConfig) {
  import Cors._

  def apply[A, B](intent: Cycle.Intent[A, B]): Cycle.Intent[A, B] = {
    case r@ OPTIONS(p) & Origin(origin) & AccessControlRequestMethod(method) =>
      val headers = AccessControlRequestHeaders(r)
      if (config.validate(origin, method, headers))
        preflight(origin, method, headers)
      else
        intent(r)
    case r@ Origin(origin) =>
      conditional(config.validateOrigin(origin)) {
        corsRequest(origin)
      } ~> intent(r)
    case r =>
      intent(r)
  }

  def preflight(origin: String, method: String, headers: List[String]) =
    Ok ~>
    Connection("keep-alive") ~>
    AccessControlAllowMethods(method) ~>
    AccessControlAllowHeaders(headers.mkString(",")) ~>
    AccessControlAllowOrigin(origin) ~>
    credentials

  def corsRequest(origin: String) =
    AccessControlAllowOrigin(origin) ~>
    credentials ~>
    expose

  def credentials =
    conditional(config.allowCredentials) {
      AccessControlAllowCredentials("true")
    }

  def expose =
    conditional(!config.exposeHeaders.isEmpty) {
      AccessControlExposeHeaders(config.exposeHeaders.mkString(","))
    }

  def cache =
    config.maxAge match {
      case None => NoOpResponder
      case Some(age) => AccessControlMaxAge(age.toString)
    }

  def conditional[A](cond: Boolean)(r: ResponseFunction[A]) =
    if (cond) r else NoOpResponder
}


object Cors {
  object Origin extends StringHeader("Origin")
  object AccessControlRequestMethod extends StringHeader("Access-Control-Request-Method")
  object AccessControlRequestHeaders extends RepeatableHeader("Access-Control-Request-Headers")

  object AccessControlAllowCredentials extends HeaderName("Access-Control-Allow-Credentials")
  object AccessControlAllowOrigin extends HeaderName("Access-Control-Allow-Origin")
  object AccessControlAllowMethods extends HeaderName("Access-Control-Allow-Methods")
  object AccessControlAllowHeaders extends HeaderName("Access-Control-Allow-Headers")
  object AccessControlExposeHeaders extends HeaderName("Access-Control-Expose-Headers")
  object AccessControlMaxAge extends HeaderName("Access-Control-Max-Age")
}
