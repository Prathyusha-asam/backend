// src/main/java/com/demo/dms/entity/RefreshToken.java
package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "`refresh_token`")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`ID`")
  private Long id;

  @Column(name = "`USER_ID`", nullable = false)
  private Integer userId;

  @Column(name = "`TOKEN_HASH`", nullable = false, unique = true, length = 200)
  private String tokenHash;

  @Column(name = "`EXPIRES_AT`", nullable = false)
  private Instant expiresAt;

  @Column(name = "`REVOKED`", nullable = false)
  private boolean revoked = false;

  @Column(name = "`replaced_by`")
  private String replacedBy;

  @Column(name = "`CREATED_AT`", nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() { return id; }
  public Integer getUserId() { return userId; }
  public void setUserId(Integer userId) { this.userId = userId; }

  public String getTokenHash() { return tokenHash; }
  public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

  public Instant getExpiresAt() { return expiresAt; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

  public boolean isRevoked() { return revoked; }
  public void setRevoked(boolean revoked) { this.revoked = revoked; }

  public String getReplacedBy() { return replacedBy; }
  public void setReplacedBy(String replacedBy) { this.replacedBy = replacedBy; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  @PrePersist
  void onCreate() {
    if (createdAt == null) createdAt = Instant.now();
  }
}
