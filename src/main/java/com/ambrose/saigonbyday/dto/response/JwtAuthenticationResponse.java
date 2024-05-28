package com.ambrose.saigonbyday.dto.response;

import com.ambrose.saigonbyday.dto.UserDTO;
import lombok.Data;

@Data
public class JwtAuthenticationResponse {
  private UserDTO userDTO;
  private String token;
  private String refreshToken;
}
