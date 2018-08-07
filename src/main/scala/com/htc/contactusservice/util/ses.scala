package com.htc.contactusservice

import java.util.concurrent.{Future => JFuture}

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.simpleemail.model.SendEmailResult
import com.twitter.inject.Logging
import com.htc.contactusservice.domain.errors.Errors._
import com.htc.contactusservice.util.PipeOperator._

import scala.concurrent.{Future, Promise}
import io.github.hamsters.Validation._
package object ses extends Logging {

  /**
    * Convert result to scala.concurrent.Future from java.util.concurrent.Future.
    */
  def wrapAsyncMethod[Request <: AmazonWebServiceRequest, Result](
      execute: AsyncHandler[Request, Result] => JFuture[Result]
  ): Future[Result] = {
    val p = Promise[Result]()
    execute {
      new AsyncHandler[Request, Result] {
        def onError(exception: Exception): Unit = {
          error(s"mail got exception ${exception}")
          p.failure(exception)
        }
        def onSuccess(request: Request, result: Result): Unit =
          p.success(result.#!("send result"))
      }
    }
    p.future
  }

}
