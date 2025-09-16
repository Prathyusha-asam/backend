package com.demo.dms.web.dto;

public record TokenPairResponse(String accessToken, String refreshToken,
                                String tokenType, long expiresInMs) {
  public static TokenPairResponse bearer(String at, String rt, long expMs) {
    return new TokenPairResponse(at, rt, "Bearer", expMs);
  }
}
