package com.htc

import com.htc.contactusservice.domain.errors.Errors

package object contactusservice {
  type Maybe[T] = Either[Errors, T]
}
