package com.htc.contactusservice.controllers

import java.io.File

import com.htc.contactusservice.domain.errors.{ContentNotFoundErrors, InternalErrors}
import com.htc.contactusservice.service.CreateContactService
import com.htc.contactusservice.util.Mail
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finagle.http.Request
import io.github.hamsters.Validation._
import io.swagger.models.Swagger
import javax.inject.{Inject, Singleton}
@Singleton
class MainController @Inject()(s: Swagger,
                               createContactService: CreateContactService,
                               createContactWithFileService: CreateContactService)
    extends SwaggerController {
  implicit protected val swagger = s

  postWithDoc("/api/devcon_contact/v1/contact_us") { o =>
    o.summary("developer can contact htc by mail")
      .tag("developer can contact htc by mail")
      .headerParam[String]("X-HTC-Account-Id", "HTC Account ID")
      .formParam[String](Mail.FROM_EMAIL, "from mail")
      .formParam[String](Mail.FROM_NAME, "from name")
      .formParam[String](Mail.TO_EMAIL, "to mail")
      .formParam[String](Mail.SUBJECT, "subject")
      .formParam[String](Mail.CONTNET, "content")
      .formParam[File](name = Mail.ATTACHMENT, description = "mail attachment file", false)
      .consumes("multipart/form-data")
      .responseWith(200, "sent mail succeed")
      .responseWith(400, "necessary field not in body")
      .responseWith(500, "internal server error")
      .description("""
                     | This API should pass the following test cases:
                     |    get 200 as sent mail succeed to Store@htcvive.com
                     |    get 400 as necessary field not in body
                     |    get 500 as internal server error
                     | __Feature test cases__
                   """.stripMargin)
  } { request: Request =>
    createContactWithFileService(request).map {
      case OK(r)                        => response.ok(r)
      case KO(l: ContentNotFoundErrors) => response.noContent
      case KO(l: InternalErrors)        => response.internalServerError(l)
      case KO(l)                        => response.badRequest(l)
    }
  }
}
