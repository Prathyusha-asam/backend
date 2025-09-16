// src/main/java/com/demo/dms/repo/RefreshTokenRepository.java
package com.demo.dms.repository;

import com.demo.dms.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByTokenHash(String tokenHash);
}
