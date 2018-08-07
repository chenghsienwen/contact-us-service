package com.htc.contactusservice.domain.errors

import com.github.mehmetakiftutuncu.errors.{CommonError, Errors}

trait Errors {
  val code: Int
  val msg: String
}

case class InternalErrors(code: Int, msg: String) extends Errors

case class ContentNotFoundErrors(code: Int, msg: String) extends Errors

case class ProcessError(code: Int, msg: String) extends Errors

case class AuthError(code: Int, msg: String) extends Errors

object Errors {

  val ParseClassErrors              = InternalErrors(code = 1003, msg = "Server Internal Error")
  val CreateAppByAppIdInternalError = InternalErrors(code = 1004, msg = "Server Internal Error")
  val DeleteAppByAppIdInternalError = InternalErrors(code = 1005, msg = "Server Internal Error")
  val GetAppByAppIdInternalError    = InternalErrors(code = 1006, msg = "Server Internal Error")
  val SendMailError                 = InternalErrors(code = 1007, msg = "Server Internal Error")

  val sesObjectNotFoundError = ContentNotFoundErrors(code = 9007, msg = "ses Object Not Found Error")
  val DeleteAppNotFound      = ContentNotFoundErrors(9009, "delete app Not Found")

  val GetAuthTokenError   = ProcessError(4009, "Get Auth Token Error")
  val DeleteS3ObjectError = ProcessError(4010, "Delete S3 Object error")

}
