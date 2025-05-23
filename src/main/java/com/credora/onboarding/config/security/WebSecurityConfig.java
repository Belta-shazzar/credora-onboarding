package com.credora.onboarding.config.security;

import com.credora.onboarding.config.security.jwt.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class  WebSecurityConfig {
  private final JwtRequestFilter jwtRequestFilter;
  private final AuthenticationProvider authenticationProvider;
  private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**", "/v1/api-docs"};

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(req -> req
                    .requestMatchers(WHITE_LIST_URL).permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions, JWT only
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
