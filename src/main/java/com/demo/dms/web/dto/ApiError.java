package com.demo.dms.web.dto;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

public record ApiError(int status, String error, String message, String path, Instant timestamp) {
  public static ApiError unauthorized(String msg) {
    return new ApiError(401, "Unauthorized", msg, "/auth/login", Instant.now());
  }
  public static ApiError forbidden(String msg) {
    return new ApiError(403, "Forbidden", msg, "/auth/login", Instant.now());
  }

}
