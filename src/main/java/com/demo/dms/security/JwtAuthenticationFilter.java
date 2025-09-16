// src/main/java/com/demo/dms/security/JwtAuthenticationFilter.java
package com.demo.dms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


  public JwtAuthenticationFilter(JwtService jwtService,
                                 @Qualifier("appUserDetailsService") UserDetailsService uds) {
    this.jwtService = jwtService;
    this.userDetailsService = uds;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain)
          throws ServletException, IOException {

    log.debug("JWT filter: {} {}", request.getMethod(), request.getRequestURI());
    log.debug("Auth header present? {}", request.getHeader("Authorization") != null);

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7).trim();
    if (token.isEmpty()) {
      chain.doFilter(request, response);
      return;
    }

    final Claims claims;
    try {
      claims = jwtService.parseAccess(token);
    } catch (JwtException e) {
      chain.doFilter(request, response);
      return;
    }

    // Optional safety: ensure this is an access token
    Object typ = claims.get("typ");
    if (typ != null && !"access".equals(typ)) {
      chain.doFilter(request, response);
      return;
    }

    String username = claims.getSubject();
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails user = userDetailsService.loadUserByUsername(username);

      var authentication =
              new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    chain.doFilter(request, response);
  }

  // Skip auth endpoints to save cycles (optional but nice)
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getRequestURI().startsWith("/auth/");
  }

}
