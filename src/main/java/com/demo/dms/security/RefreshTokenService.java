// src/main/java/com/demo/dms/security/RefreshTokenService.java
package com.demo.dms.security;

import com.demo.dms.entity.RefreshToken;
import com.demo.dms.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {
  private final RefreshTokenRepository repo;

  public RefreshTokenService(RefreshTokenRepository repo) { this.repo = repo; }

  public String newJti() { return UUID.randomUUID().toString(); }

  public static String sha256B64(String value) {
    try {
      var md = MessageDigest.getInstance("SHA-256");
      return Base64.getEncoder().encodeToString(md.digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) { throw new IllegalStateException(e); }
  }

  public RefreshToken saveRaw(Integer userId, String rawRefresh, long expiresInMs) {
    var rt = new RefreshToken();
    rt.setUserId(userId);
    rt.setTokenHash(sha256B64(rawRefresh));
    rt.setExpiresAt(Instant.now().plusMillis(expiresInMs));
    return repo.save(rt);
  }

  public RefreshToken validateUsable(String rawToken) {
    var hash = sha256B64(rawToken);
    var rt = repo.findByTokenHash(hash).orElseThrow(() -> new IllegalArgumentException("Invalid refresh"));
    if (rt.isRevoked()) throw new IllegalArgumentException("Refresh revoked");
    if (rt.getExpiresAt().isBefore(Instant.now())) throw new IllegalArgumentException("Refresh expired");
    return rt;
  }

  public RefreshToken rotate(RefreshToken current, String newRaw) {
    var newHash = sha256B64(newRaw);
    current.setRevoked(true);
    current.setReplacedBy(newHash);
    repo.save(current);
    var next = new RefreshToken();
    next.setUserId(current.getUserId());
    next.setTokenHash(newHash);
    next.setExpiresAt(current.getExpiresAt()); // same expiry window; or set new one if you prefer
    return repo.save(next);
  }

  public void revokeAllForUser(Integer userId) {
    repo.findAll().stream()
        .filter(rt -> rt.getUserId().equals(userId) && !rt.isRevoked())
        .forEach(rt -> { rt.setRevoked(true); repo.save(rt); });
  }
}
