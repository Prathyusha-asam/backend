package com.demo.dms.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtKeysConfig {

  @Bean
  @Qualifier("accessKey")
  public SecretKey accessKey(@Value("${app.jwt.access-secret}") String s) {
    return Keys.hmacShaKeyFor(s.getBytes(StandardCharsets.UTF_8));
  }

  @Bean
  @Qualifier("refreshKey")
  public SecretKey refreshKey(@Value("${app.jwt.refresh-secret}") String s) {
    return Keys.hmacShaKeyFor(s.getBytes(StandardCharsets.UTF_8));
  }
}
