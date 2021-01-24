package com.vertx.commons.web.server.response;

import java.util.List;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.json.schema.ValidationException;
import io.vertx.serviceproxy.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DataObject(publicConverter = true)
public class FailureResponse {

  private int code;
  private String message;
  private List<String> details;

  public FailureResponse create(Throwable cause) {
    this.message = cause.getMessage();
    return this;
  }

  public FailureResponse create(ValidationException cause) {
    this.message = cause.keyword() + " - " + cause.getMessage();
    return this;
  }

  public FailureResponse create(ServiceException cause) {
    this.code = cause.failureCode();

    if (cause.failureType() == ReplyFailure.RECIPIENT_FAILURE) {
      this.getMessageFromServiceCause(cause);
    } else {
      this.message = cause.getMessage();
    }

    return this;
  }

  private void getMessageFromServiceCause(ServiceException cause) {
    if (cause.getMessage() != null) {
      this.message = "Error in operation: " + cause.getMessage();
    } else if ("null".equalsIgnoreCase(cause.getMessage())) {
      this.message = "Error in operation: NullPointerException";
    } else {
      this.message = "Unknown error during operation. It occurs when exceptions weren't treated in execution.";
    }
  }
}