package com.htc.contactusservice

import com.htc.contactusservice.modules.{CustomJacksonModule, ServiceSwaggerModule}
import com.htc.contactusservice.controllers.AdminController
import com.htc.contactusservice.controllers.MainController
import com.htc.contactusservice.util.AppConfigLib._
import com.jakehschwartz.finatra.swagger.DocsController
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.util.Var
import perfolation._

object ServerMain extends Server

class Server extends HttpServer {
  val health                     = Var("good")
  override def jacksonModule     = CustomJacksonModule
  override protected def modules = Seq(ServiceSwaggerModule)

  override def defaultFinatraHttpPort = getConfig[String]("FINATRA_HTTP_PORT").fold(":8888")(x => p":$x")
  override val name                   = getClass.getPackage.getImplementationTitle

  override def configureHttp(router: HttpRouter): Unit =
    router
      .filter[CommonFilters]
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .add[DocsController]
      .add[AdminController]
      .add[MainController]
}
