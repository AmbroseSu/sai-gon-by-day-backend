package com.ambrose.saigonbyday.services;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
  String extractUserName(String token);
  String extractRoles(String token);

  String generateToken(UserDetails userDetails);

  String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails);
  boolean isTokenValid(String token, UserDetails userDetails);
}
