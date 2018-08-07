package com.htc.contactusservice.util

import com.amazonaws.regions.{Region, Regions}
import com.twitter.inject.Logging

class AWSProvider extends Logging {

  def getSESClient(): Option[SESClient] =
    (for {
      awsAccessKeyId <- sys.env.get("AWS_ACCESS_KEY_ID")
      awsSecretKey   <- sys.env.get("AWS_SECRET_ACCESS_KEY")

    } yield {
      implicit val region = Regions.US_WEST_2
      info(s"s3 information: ${awsAccessKeyId}")
      SESClient(awsAccessKeyId, awsSecretKey)
    })

  def getBucket(): String = sys.env.get("AWS_S3_BUCKET").getOrElse("")
}
