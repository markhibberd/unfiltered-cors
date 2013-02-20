package io.mth.unfiltered.cors

import argonaut._, Argonaut._
import unfiltered.request._
import unfiltered.response._
import Cors._

class TestApp extends unfiltered.filter.Plan {
  case class Data(value: String)

  implicit def DataEncodeJson: EncodeJson[Data] =
    jencode1L((d: Data) => d.value)("data")
  implicit def DataDecodeJson: DecodeJson[Data] =
    jdecode1L(Data.apply)("data")

  object ArgonautBody {
    def apply[T](r: HttpRequest[T]) = new {
      def decode[A: DecodeJson] =
        Body.string(r).decode[A]
      def json =
        Body.string(r).parse
    }
  }

  val ref = new java.util.concurrent.atomic.AtomicReference("initial")

  val cors = Cors(
    CorsConfig.basic("http://localhost")
  )

  def intent = cors {
    case GET(Path("/yoyo")) =>
      Ok ~> JsonContent ~>
        ResponseString(Data(ref.get).jencode.spaces2)
    case r@PUT(Path("/yoyo")) =>
      ArgonautBody(r).decode[Data].fold(
        _ => JsonContent ~> BadRequest ~> ResponseString(
          (("error" := "could not process request") ->: jEmptyObject).jencode.spaces2),
        d => {
          ref.set(d.value)
          NoContent
        }
      )
  }

}
