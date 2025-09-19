package com.demo.dms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {
  private AuthUtils() {}

  public static String currentEmail() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    return (a != null ? a.getName() : null);
  }

  public static boolean hasRole(String role) {
    var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
  }

}
