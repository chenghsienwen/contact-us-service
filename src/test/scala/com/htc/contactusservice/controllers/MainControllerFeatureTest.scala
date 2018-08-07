package com.htc.contactusservice.controllers

import java.io.File
import java.nio.file.{Files, Paths}

import com.twitter.finagle.http.Status.Ok
import com.twitter.finatra.http.{EmbeddedHttpServer, HttpMockResponses}
import com.twitter.inject.server.FeatureTest
import com.htc.contactusservice.Server
import com.htc.contactusservice.util.{Mail, TimestampUtils}
import com.twitter.finagle.http.{FileElement, SimpleElement}
import cats.syntax.option._
import com.twitter.inject.Mockito
import com.twitter.io.Buf.ByteArray

/**
  * sbt "testOnly com.htc.contactusservice.controllers.MainControllerFeatureTest"
  * sbt 'testOnly com.htc.contactusservice.controllers.MainControllerFeatureTest -- -z "return 200 as send succeed"'
  */
class MainControllerFeatureTest extends FeatureTest with HttpMockResponses with Mockito{
  override val server = new EmbeddedHttpServer(new Server)
  val classPath       = System.getenv("PWD")
  val testMail        = "test@gmail.com"
  test("return 200 as send succeed") {
    val filePath  = s"$classPath/src/test/resources/test.png"
    val byteArray = Files.readAllBytes(Paths.get(filePath))
    val params = List(
      SimpleElement(Mail.FROM_EMAIL, testMail),
      SimpleElement(Mail.FROM_NAME, "test"),
      SimpleElement(Mail.TO_EMAIL, testMail),
      SimpleElement(Mail.SUBJECT, "subject"),
      SimpleElement(Mail.CONTNET, "content"),
      FileElement(Mail.ATTACHMENT, new ByteArray(byteArray, 0, byteArray.length), "image/png".some, "test.png".some)
    )
    server.httpMultipartFormPost(
      path = "/api/devcon_contact/v1/contact_us",
      params = params,
      multipart = true,
      andExpect = Ok,
      withBody = "{}"
    )
  }
}
