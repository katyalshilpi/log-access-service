package com.jpmc.accessor.logs.v1.errors;

import javax.ws.rs.core.Response;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A default exception handler for services. It is intended to reduce boiler plate code in services for common behavior.
 */
@ControllerAdvice
public class DefaultExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponseBody handleInternalError(Exception e) {
    return new ErrorResponseBody(Response.Status.INTERNAL_SERVER_ERROR, e);
  }

}
