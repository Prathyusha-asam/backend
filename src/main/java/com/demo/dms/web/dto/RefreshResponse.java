package com.demo.dms.web.dto;

public record RefreshResponse(String accessToken, String tokenType, long expiresInMs) {}
