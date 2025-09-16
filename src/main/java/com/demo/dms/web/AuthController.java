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

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req) {

    /*try {
      authManager.authenticate(
              new UsernamePasswordAuthenticationToken(req.email(), req.password()));
    } catch (org.springframework.security.authentication.BadCredentialsException
             | org.springframework.security.core.userdetails.UsernameNotFoundException e) {
      return ResponseEntity.status(401).body(ApiError.unauthorized("Invalid email or password"));
    } catch (org.springframework.security.authentication.DisabledException e) {
      return ResponseEntity.status(403).body(ApiError.forbidden("User is disabled"));
    } catch (org.springframework.security.authentication.LockedException e) {
      return ResponseEntity.status(403).body(ApiError.forbidden("User is locked"));
    }*/

    authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.email(), req.password()));

    UserDetails user = uds.loadUserByUsername(req.email());
    String accessJti = UUID.randomUUID().toString();
    String refreshJti = UUID.randomUUID().toString();

    String access = jwt.generateAccess(user, accessJti);
    String refresh = jwt.generateRefresh(user, refreshJti);

    // Persist hashed refresh
    Integer userId = userRepo.findByEmailIgnoreCase(req.email()).map(UserAccount::getUserId).orElseThrow();
    refreshSvc.saveRaw(userId, refresh, refreshExpMs);

    return ResponseEntity.ok(TokenPairResponse.bearer(access, refresh, accessExpMs));
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
