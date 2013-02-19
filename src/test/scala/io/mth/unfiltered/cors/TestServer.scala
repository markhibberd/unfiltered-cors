package io.mth.unfiltered.cors

object TestServer {
  def main(args: Array[String]) {
    val http = unfiltered.jetty.Http(10000)
    http.filter(new TestApp).run({ svr =>
        println("server started at " + http.url)
      }, { svr =>
        println("shutting down server")
      })
  }
}
