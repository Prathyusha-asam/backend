package com.demo.dms.web;

import com.demo.dms.web.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiError badCreds(HttpServletRequest req) {
    return new ApiError(401, "Unauthorized", "Invalid email or password", req.getRequestURI(), java.time.Instant.now());
  }

  @ExceptionHandler({DisabledException.class, LockedException.class})
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiError userState(HttpServletRequest req, Exception ex) {
    String msg = ex instanceof DisabledException ? "User is disabled" : "User is locked";
    return new ApiError(403, "Forbidden", msg, req.getRequestURI(), java.time.Instant.now());
  }

  // catch-all for any other auth error
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiError authGeneric(HttpServletRequest req) {
    return new ApiError(401, "Unauthorized", "Authentication failed", req.getRequestURI(), java.time.Instant.now());
  }

  @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
  public org.springframework.http.ResponseEntity<ApiError> handleRSE(
          jakarta.servlet.http.HttpServletRequest req,
          org.springframework.web.server.ResponseStatusException ex) {

    var status = ex.getStatusCode();
    var body = new ApiError(status.value(),
            status.is4xxClientError() ? "Bad Request" : "Error",
            ex.getReason() != null ? ex.getReason() : "Request failed",
            req.getRequestURI(),
            java.time.Instant.now());

    return org.springframework.http.ResponseEntity.status(status).body(body);
  }

}
