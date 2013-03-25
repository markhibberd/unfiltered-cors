package io.mth.unfiltered.cors

import unfiltered.request.{Connection => _, _}
import unfiltered.response._
import unfiltered.kit._
import unfiltered.Cycle

case class CorsConfig(
  validateOrigin: String => Boolean,
  validateMethod: String => Boolean,
  validateHeaders: List[String] => Boolean,
  allowCredentials: Boolean,
  maxAge: Option[Int],
  exposeHeaders: List[String]
) {
  def validate(origin: String, method: String, headers: List[String]) =
    validateOrigin(origin) && validateMethod(method) && validateHeaders(headers)
}

object CorsConfig {
  def origins(origins: String*) = CorsConfig(
    origins.contains(_),
    (_: String) => true,
    (_: List[String]) => true,
    true,
    Some(120),
    Nil
  )
}
