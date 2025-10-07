// security/AppUserDetailsService.java
package com.demo.dms.service;

import com.demo.dms.entity.UserAccount;
import com.demo.dms.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;

@Service
public class AppUserDetailsService implements UserDetailsService {

  private final UserAccountRepository userRepository;

  @Autowired
  public AppUserDetailsService(UserAccountRepository userRepository) {

    this.userRepository = userRepository;
  }

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    System.out.println("=== loadUserByUsername called ===");
    System.out.println("Looking for username: " + username);
    // Test findAll first
    long count = userRepository.count();
    System.out.println("Total users in DB: " + count);

    UserAccount u = userRepository.findByEmailIgnoreCase(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    System.out.println("User loaded: " + u.getEmail() + " - " + u.getFullName());

    String[] authorities = Arrays.stream((u.getRole() == null ? "USER" : u.getRole())
                    .split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(r -> "ROLE_" + r)
            .toArray(String[]::new);
    System.out.println("Authorities: " + Arrays.toString(authorities));

    return User.withUsername(u.getEmail())
            .password(u.getPassword())//ensure it is already encoded
            .authorities(authorities)
            .accountExpired(false).accountLocked(false)
            .credentialsExpired(false).disabled(false)
            .build();
  }
}
