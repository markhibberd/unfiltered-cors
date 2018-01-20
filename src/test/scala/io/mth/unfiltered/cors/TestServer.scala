package io.mth.unfiltered.cors

object TestServer {
  def main(args: Array[String]) {
    val http = unfiltered.jetty.Server.http(10000)
    http.plan(new TestApp).run({ svr =>
        println(s"server started at http://localhost:${svr.ports.head}")
      }, { svr =>
        println("shutting down server")
      })
  }
}
