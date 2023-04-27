package com.jpmc.accessor.logs.v1.errors;

import javax.ws.rs.core.Response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Message structure for standardized error response.
 */
@Data
@Slf4j
@AllArgsConstructor
public class ErrorResponseBody  {
  private String message;
  private int code;

  public ErrorResponseBody(String msg) {
    this.message = msg;
  }
  public ErrorResponseBody(Response.Status status, Throwable error) {
    this(status.getReasonPhrase() + " - " + error.getMessage());
    code = status.getStatusCode();
    logSomething(error);
  }

  public ErrorResponseBody(Response.Status status, String extraMessage) {
    this(status.getReasonPhrase() + " - " + extraMessage);
    code = status.getStatusCode();
    logSomething(null);
  }

  public ErrorResponseBody(Response.Status status, String extraMessage, Throwable error) {
    this(status.getReasonPhrase() + " - " + extraMessage + " - " + error.getMessage());
    code = status.getStatusCode();
    logSomething(error);
  }

  public ErrorResponseBody(HttpStatus status, Throwable error) {
    this(status.getReasonPhrase() + " - " + error.getMessage());
    code = status.value();
    logSomething(error);
  }

  public ErrorResponseBody(int status, String statusPhrase, String extraMessage, Throwable error) {
    this(statusPhrase + " - " + extraMessage + " - " + error.getMessage());
    code = status;
    logSomething(error);
  }

  public ErrorResponseBody(int status, String message) {
    this(message);
    code = status;
    logSomething(null);
  }

  private void logSomething(Throwable error) {
    if (log.isDebugEnabled()) {
      log.debug(getMessage(), error);
    } else if (error == null) {
      log.info(getMessage());
    } else if (code >= 500) {
      log.error(getMessage(), error);
    } else {
      log.info(getMessage());
    }
  }

}
