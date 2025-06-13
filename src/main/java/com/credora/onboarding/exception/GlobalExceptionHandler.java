package com.credora.onboarding.exception;

import com.credora.onboarding.exception.custom.BadRequestException;
import com.credora.onboarding.exception.custom.ConflictException;
import com.credora.onboarding.exception.custom.NotFoundException;
import com.credora.onboarding.exception.custom.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ResponseStatus
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorMessage> conflictException(ConflictException ex) {
    ErrorMessage message = new ErrorMessage(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT,
            ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorMessage> badRequestException(BadRequestException ex) {
    ErrorMessage message = new ErrorMessage(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorMessage> notFoundException(NotFoundException ex) {
    ErrorMessage message = new ErrorMessage(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND,
            ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorMessage> unauthorizedException(UnauthorizedException ex) {
    ErrorMessage message = new ErrorMessage(
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED,
            ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
          MethodArgumentNotValidException ex,
          HttpServletRequest request
  ) {
    Map<String, Object> response = new HashMap<>();

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Bad Request");
    response.put("message", "Validation failed");
    response.put("errors", errors);
    response.put("path", request.getRequestURI());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllExceptions(
          Exception ex,
          HttpServletRequest request
  ) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", "Internal Server Error");
    body.put("message", ex.getMessage());
    body.put("path", request.getRequestURI());

    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
