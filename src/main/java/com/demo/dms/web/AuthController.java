// src/main/java/com/demo/dms/web/AuthController.java
package com.demo.dms.web;

import com.demo.dms.entity.UserAccount;
import com.demo.dms.repository.UserAccountRepository;
import com.demo.dms.security.JwtService;
import com.demo.dms.security.RefreshTokenService;
import com.demo.dms.web.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

  private final AuthenticationManager authManager;
  private final UserDetailsService uds;
  private final JwtService jwt;
  private final RefreshTokenService refreshSvc;
  private final UserAccountRepository userRepo;

  @Value("${app.jwt.access-expiration-ms}") long accessExpMs;
  @Value("${app.jwt.refresh-expiration-ms}") long refreshExpMs;


  public AuthController(AuthenticationManager authManager, UserDetailsService uds,
                        JwtService jwt, RefreshTokenService refreshSvc,
                        UserAccountRepository userRepo) {
    this.authManager = authManager;
    this.uds = uds;
    this.jwt = jwt;
    this.refreshSvc = refreshSvc;
    this.userRepo = userRepo;
  }

  @GetMapping("/test-user")
  public ResponseEntity<?> testUser() {
    System.out.println("=== TEST USER ENDPOINT CALLED ===");
    System.out.println("Looking for email: netravati.k@saksoft.com");

    try {
      Optional<UserAccount> user = userRepo.findByEmail("netravati.k@saksoft.com");

      if (user.isPresent()) {
        System.out.println("USER FOUND: " + user.get().getFullName());
        return ResponseEntity.ok("User found: " + user.get().getFullName());
      } else {
        System.out.println("USER NOT FOUND");
        return ResponseEntity.ok("User NOT found in database");
      }
    } catch (Exception e) {
      System.out.println("ERROR: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req) {

    authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.email(), req.password()));

    UserDetails user = uds.loadUserByUsername(req.email());
    String accessJti = UUID.randomUUID().toString();
    String refreshJti = UUID.randomUUID().toString();

    String access = jwt.generateAccess(user, accessJti);
    String refresh = jwt.generateRefresh(user, refreshJti);

    // Persist hashed refresh
    System.out.println( " =====User details for all===== " + userRepo.findAll());
    List<UserAccount> ua = userRepo.findAll();
    for (UserAccount s : ua) {
      System.out.println("Email id = " + s.getEmail() + " Password = " +s.getPassword());
    }

    Optional<UserAccount> userAccount = userRepo.findByEmailIgnoreCase(req.email());
    System.out.println("User details -> " + userAccount.get().getUserId());
    Integer userId = userAccount.map(UserAccount::getUserId).orElseThrow();
    refreshSvc.saveRaw(userId, refresh, refreshExpMs);
    return ResponseEntity.ok(TokenPairLoginResponse.bearer(access,refresh,accessExpMs,userAccount));

  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenPairResponse> refresh(@RequestBody RefreshRequest body) {
    // 1) Verify the refresh token structure & signature
    var claims = jwt.parseRefresh(body.refreshToken());
    String email = claims.getSubject();

    // 2) Check DB (hash), expiry, and not revoked
    var record = refreshSvc.validateUsable(body.refreshToken());

    // 3) Load user & mint new pair (rotate refresh)
    var user = uds.loadUserByUsername(email);
    String newAccess = jwt.generateAccess(user, UUID.randomUUID().toString());
    String newRefresh = jwt.generateRefresh(user, UUID.randomUUID().toString());
    refreshSvc.rotate(record, newRefresh);

    return ResponseEntity.ok(TokenPairResponse.bearer(newAccess, newRefresh, accessExpMs));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestBody RefreshRequest body) {
    // Best effort revoke the presented refresh
    try {
      var rec = refreshSvc.validateUsable(body.refreshToken());
      rec.setRevoked(true);
      // Save via repo or add a revoke method; omitted for brevity
    } catch (Exception ignored) {}
    return ResponseEntity.noContent().build();
  }
}
