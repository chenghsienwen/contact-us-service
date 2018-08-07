package com.htc.contactusservice.domain

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.Properties

import com.amazonaws.services.simpleemail.model.RawMessage
import javax.activation.{DataHandler, FileDataSource}
import javax.mail.{Message, Session}
import javax.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart, MimeUtility}
import javax.mail.util.ByteArrayDataSource

case class Email(
    fromEmail: String,
    fromName: String,
    toEmail: String,
    subject: String,
    content: EmailContent,
    attachments: Seq[Attachment] = Seq.empty,
    headers: Map[String, String] = Map()
) {

  def toRawMessage: RawMessage = {
    val session = Session.getDefaultInstance(new Properties())
    val message = new MimeMessage(session)
    message.setSubject(subject, "UTF-8")
    message.setFrom(new InternetAddress(fromEmail, fromName))
    message.setRecipients(Message.RecipientType.TO, toEmail)
    message.setContent(createMessageContent)

    headers.foreach { case (h, v) => message.setHeader(h, v) }

    val output = new ByteArrayOutputStream()
    message.writeTo(output)
    new RawMessage(ByteBuffer.wrap(output.toByteArray()))
  }

  private def createMessageContent: MimeMultipart = {
    val defaultCharSet = MimeUtility.getDefaultJavaCharset()

    val messageContainer = new MimeMultipart("mixed")
    val textWrapper      = new MimeBodyPart()
    val textBody         = new MimeMultipart("alternative")
    textWrapper.setContent(textBody)
    messageContainer.addBodyPart(textWrapper)

    val (textContent, contentMime) = content match {
      case Text(text) => (text, "text/plain; charset=UTF-8")
      case HTML(html) => (html, "text/html; charset=UTF-8")
    }

    val textPart = new MimeBodyPart()
    textPart.setContent(textContent, contentMime)
    textPart.setHeader("Content-Transfer-Encoding", "base64")
    textBody.addBodyPart(textPart)

    attachments.foreach {
      case Attachment(fileName, path) =>
        val att = new MimeBodyPart()
        val fds = new FileDataSource(path)
        att.setDataHandler(new DataHandler(fds))
        att.setFileName(fileName)
        messageContainer.addBodyPart(att)
    }

    messageContainer
  }
}

sealed trait EmailContent
case class Text(value: String) extends EmailContent
case class HTML(value: String) extends EmailContent

case class Attachment(fileName: String, path: String)
