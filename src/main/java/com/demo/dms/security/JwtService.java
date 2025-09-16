package com.demo.dms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;     // <-- type of both keys
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

@Service
public class  JwtService {

  private final SecretKey accessKey;            // <-- SecretKey, not java.security.Key
  private final SecretKey refreshKey;
  private final long accessExpMs;
  private final long refreshExpMs;

  public JwtService(
      @Value("${app.jwt.access-secret}") String accessSecret,
      @Value("${app.jwt.refresh-secret}") String refreshSecret,
      @Value("${app.jwt.access-expiration-ms}") long accessExpMs,
      @Value("${app.jwt.refresh-expiration-ms}") long refreshExpMs) {

    this.accessKey  = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
    this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    this.accessExpMs = accessExpMs;
    this.refreshExpMs = refreshExpMs;
  }

  public String generateAccess(UserDetails user, String jti) {
    var now = new Date();
    return Jwts.builder()
            .subject(user.getUsername())
            .claims(Map.of(
                    "typ", "access",
                    "roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                    "jti", jti
            ))
            .issuedAt(now)
            .expiration(new Date(now.getTime() + accessExpMs))
            .signWith(accessKey)     // 0.12.x: only the key, no algorithm arg
            .compact();
  }

  public String generateRefresh(UserDetails user, String jti) {
    var now = new Date();
    return Jwts.builder()
            .subject(user.getUsername())
            .claims(Map.of("typ", "refresh", "jti", jti))
            .issuedAt(now)
            .expiration(new Date(now.getTime() + refreshExpMs))
            .signWith(refreshKey)    // uses the second key
            .compact();
  }

  public Claims parseAccess(String token) {
    return Jwts.parser().verifyWith((SecretKey) accessKey).build()
            .parseSignedClaims(token).getPayload();
  }

  public Claims parseRefresh(String token) {
    return Jwts.parser().verifyWith((SecretKey) refreshKey).build()
            .parseSignedClaims(token).getPayload();
  }
}
