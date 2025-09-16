// com.demo.dms.security.SecurityConfig
package com.demo.dms.security;

import com.demo.dms.web.dto.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Instant;

@Configuration
public class SecurityConfig {

  @Bean
  public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
    return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
  }


  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
    return cfg.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                 JwtAuthenticationFilter jwtFilter, ObjectMapper mapper) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})                    // keep if you call from a browser frontend
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**", "/actuator/health", "/error").permitAll()
                    .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                    .anyRequest().authenticated()
            )

            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) -> {
                      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                      res.setContentType("application/json");
                      mapper.writeValue(res.getOutputStream(),
                              new ApiError(401, "Unauthorized", "Invalid or missing token",
                                      req.getRequestURI(), Instant.now()));
                    })
                    .accessDeniedHandler((req, res, e) -> {
                      res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                      res.setContentType("application/json");
                      mapper.writeValue(res.getOutputStream(),
                              new ApiError(403, "Forbidden", "Insufficient permissions",
                                      req.getRequestURI(), Instant.now()));
                    })
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public org.springframework.boot.CommandLineRunner printHash(PasswordEncoder pe) {
    return args -> System.out.println("BCrypt: " + pe.encode("Netra@123"));
  }

  // in SecurityConfig
  @Bean
  public org.springframework.security.web.AuthenticationEntryPoint restEntryPoint(
          com.fasterxml.jackson.databind.ObjectMapper mapper) {
    return (req, res, ex) -> {
      res.setStatus(401);
      res.setContentType("application/json");
      mapper.writeValue(res.getOutputStream(),
              new com.demo.dms.web.dto.ApiError(401, "Unauthorized",
                      "Invalid or missing token", req.getRequestURI(), java.time.Instant.now()));
    };
  }

  @Bean
  public org.springframework.security.web.access.AccessDeniedHandler restDenied(
          com.fasterxml.jackson.databind.ObjectMapper mapper) {
    return (req, res, ex) -> {
      res.setStatus(403);
      res.setContentType("application/json");
      mapper.writeValue(res.getOutputStream(),
              new com.demo.dms.web.dto.ApiError(403, "Forbidden",
                      "Insufficient permissions", req.getRequestURI(), java.time.Instant.now()));
    };
  }


}
