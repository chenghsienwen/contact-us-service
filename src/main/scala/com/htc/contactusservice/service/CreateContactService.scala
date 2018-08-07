package com.htc.contactusservice.service

import java.io.{BufferedOutputStream, FileOutputStream}

import com.htc.contactusservice.Maybe
import com.htc.contactusservice.domain._
import com.htc.contactusservice.domain.errors.Errors._
import com.htc.contactusservice.domain.http.CreateContactResponse
import com.htc.contactusservice.service.Implicits._
import com.htc.contactusservice.util.PipeOperator._
import com.htc.contactusservice.util.{Mail, TimestampUtils}
import com.twitter.finagle.Service
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.fileupload.MultipartItem
import com.twitter.finatra.http.request.RequestUtils
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.Logging
import com.twitter.util.Future
import io.github.hamsters.Validation._
import javax.inject.Inject

class CreateContactService @Inject()(timestampUtils: TimestampUtils, objectMapper: FinatraObjectMapper)
    extends Service[Request, Maybe[CreateContactResponse]]
    with Logging {

  val address = "chenghsienwen@gmail.com"
  override def apply(req: Request): Future[Maybe[CreateContactResponse]] =
    sesClient match {
      case Some(r) => {
        r.send(provideEmail(req))
        CreateContactResponse(None) |> (OK(_)) |> (Future(_))
      }
      case None => KO(sesObjectNotFoundError) |> (Future(_))
    }

  val provideEmail: (Request) => Email = { req =>
    val multiparts = RequestUtils.multiParams(req)
    val attachments = multiparts
      .filter(_._2.contentType.getOrElse("").contains("image"))
      .map { i =>
        val fileName = i._2.filename.getOrElse("unknown")
        val path     = classPath + "/" + timestampUtils.currentTimestamp() + "_" + fileName
        val bos      = new BufferedOutputStream(new FileOutputStream(path))
        bos.write(i._2.data)
        bos.close()
        Attachment(fileName, path)
      }
      .toList
    Email(
      fromEmail = convertTextField(multiparts, Mail.FROM_EMAIL, address),
      fromName = convertTextField(multiparts, Mail.FROM_NAME, "unknown"),
      toEmail = convertTextField(multiparts, Mail.TO_EMAIL, address),
      subject = convertTextField(multiparts, Mail.SUBJECT, "unknown"),
      content = Text(convertTextField(multiparts, Mail.CONTNET, "emptyContent")),
      attachments = attachments,
      headers = Map()
    )
  }
  val convertTextField: (Map[String, MultipartItem], String, String) => String = { (multiparts, key, default) =>
    (for {
      value <- multiparts.get(key)
    } yield {
      value.data.map(_.toChar).mkString
    }).getOrElse(default)
  }
}
