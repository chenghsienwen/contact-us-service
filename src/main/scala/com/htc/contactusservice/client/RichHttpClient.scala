package com.htc.contactusservice.client

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.conversions.time._
import com.htc.contactusservice.util.AppConfigLib._
import com.htc.contactusservice.util.PipeOperator._

object RichHttpClient {
  /* Public */
  val shouldEnableFastFail =
    getConfig[Boolean]("FAIL_FAST_ENABLE").getOrElse(false)

  def newClientService(dest: String): Service[Request, Response] =
    Http.client.withSession
      .maxLifeTime(20.seconds)
      .withSession
      .maxIdleTime(10.seconds)
      .|>(
        c =>
          if (!shouldEnableFastFail) c.withSessionQualifier.noFailFast
          else c
      )
      .newService(dest)

  def newSslClientService(sslHostname: String, dest: String): Service[Request, Response] = {
    val shouldValidate = getConfig[Boolean]("TLS_VALIDATION").getOrElse(true)

    Http.client.withSession
      .maxLifeTime(20.seconds)
      .withSession
      .maxIdleTime(10.seconds)
      .|>(
        c =>
          if (!shouldEnableFastFail) c.withSessionQualifier.noFailFast
          else c
      )
      .|>(
        c =>
          if (shouldValidate) c.withTls(sslHostname)
          else c.withTlsWithoutValidation
      )
      .newService(dest)
  }
}
