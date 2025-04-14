package com.credora.onboarding.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflictException extends Exception {

  @Serial
  private static final long serialVersionUID = 1L;

  public ConflictException(String message) {
    super(message);
  }
}
