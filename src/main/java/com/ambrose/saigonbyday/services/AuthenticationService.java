package com.ambrose.saigonbyday.services;


import com.ambrose.saigonbyday.dto.request.RefreshTokenRequest;
import com.ambrose.saigonbyday.dto.request.SignUp;
import com.ambrose.saigonbyday.dto.request.SigninRequest;
import com.ambrose.saigonbyday.entities.User;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
  ResponseEntity<?> signin(SigninRequest signinRequest);

  ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest);

  public ResponseEntity<?> checkEmail(String email);
  public String checkResetVerifyToken(String email, Long id);
  public ResponseEntity<?> saveInfor(SignUp signUp);
}
