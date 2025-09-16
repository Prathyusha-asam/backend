// com.demo.dms.security.SecurityConfig
package com.demo.dms.security;

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
                                                 JwtAuthenticationFilter jwtFilter) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})                    // keep if you call from a browser frontend
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**").permitAll()       // <-- allow login/refresh
                    .requestMatchers("/actuator/health").permitAll()
                    // (dev only) open a test GET if you want:
                    // .requestMatchers(HttpMethod.GET, "/dms/ticket-details").permitAll()
                    .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public org.springframework.boot.CommandLineRunner printHash(PasswordEncoder pe) {
    return args -> System.out.println("BCrypt: " + pe.encode("Netra@123"));
  }
}
