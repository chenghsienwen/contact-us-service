package com.htc.contactusservice.service

import com.amazonaws.regions.{Region, Regions}
import com.htc.contactusservice.util.AWSProvider

object Implicits {
  implicit val classPath = System.getenv("PWD")
  implicit val sesClient = new AWSProvider().getSESClient()
}
