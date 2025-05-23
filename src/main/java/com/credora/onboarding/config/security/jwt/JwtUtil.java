package com.credora.onboarding.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
  @Value("${application.security.jwt.expiration}")
  private long tokenExpiration;

  @Value("${application.security.jwt.refreshTokenExpiration}")
  private long refreshTokenExpiration;

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  public String getUserNameFromToken(String token) {
    return getClaimsFromToken(token, Claims::getSubject);
  }

  public <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver) {
      final Claims claims = getAllClaimsFromToken(token);
      return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUserNameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private Boolean isTokenExpired(String token) {
    final Date expiryDate = getExpirationFromToken(token);
    return expiryDate.before(new Date());
  }

  private Date getExpirationFromToken(String token) {
    return getClaimsFromToken(token, Claims::getExpiration);
  }

  public String generateAccessToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(
                    Collectors.toList()
            )
    );

    return generateToken(claims, userDetails, tokenExpiration);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails, refreshTokenExpiration);
  }

  public String generateToken(
          Map<String, Object> claims,
          UserDetails userDetails,
          long expiration
  ) {
    return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
