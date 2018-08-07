package com.htc.contactusservice.util

import java.util.concurrent.{Future => JFuture}

import com.amazonaws.auth._
import com.amazonaws.client.builder.ExecutorFactory
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.model.{SendRawEmailRequest, SendRawEmailResult}
import com.amazonaws.services.simpleemail.{AmazonSimpleEmailServiceAsync, AmazonSimpleEmailServiceAsyncClientBuilder}
import com.htc.contactusservice.domain.Email
import com.htc.contactusservice.ses

import scala.concurrent.Future
trait SES { self: SESClient =>

  def send(email: Email): Future[SendRawEmailResult] =
    ses.wrapAsyncMethod {
      aws.sendRawEmailAsync(
        new SendRawEmailRequest(email.toRawMessage),
        _: AsyncHandler[SendRawEmailRequest, SendRawEmailResult]
      )
    }
}

object SESClient {

  def apply(accessKeyId: String, secretKeyId: String)(implicit region: Regions): SESClient =
    apply(new BasicAWSCredentials(accessKeyId, secretKeyId))

  def apply(awsCredentials: AWSCredentials = new AnonymousAWSCredentials)(implicit region: Regions): SESClient =
    apply(new AWSStaticCredentialsProvider(awsCredentials))

  def apply(awsCredentials: AWSCredentials, executorFactory: ExecutorFactory)(implicit region: Regions): SESClient =
    apply(new AWSStaticCredentialsProvider(awsCredentials), executorFactory)

  def apply(awsCredentialsProvider: AWSCredentialsProvider)(implicit region: Regions): SESClient = {
    val client = AmazonSimpleEmailServiceAsyncClientBuilder.standard
      .withCredentials(awsCredentialsProvider)
      .withRegion(region)
      .build()
    new SESClient(client)
  }

  def apply(awsCredentialsProvider: AWSCredentialsProvider,
            executorFactory: ExecutorFactory)(implicit region: Regions): SESClient = {
    val client = AmazonSimpleEmailServiceAsyncClientBuilder.standard
      .withCredentials(awsCredentialsProvider)
      .withExecutorFactory(executorFactory)
      .withRegion(region)
      .build()
    new SESClient(client)
  }

}

class SESClient(val aws: AmazonSimpleEmailServiceAsync) extends SES
