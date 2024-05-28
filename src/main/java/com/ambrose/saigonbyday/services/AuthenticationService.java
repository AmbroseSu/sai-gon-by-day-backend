package com.ambrose.saigonbyday.services;


import com.ambrose.saigonbyday.dto.request.RefreshTokenRequest;
import com.ambrose.saigonbyday.dto.request.SignUp;
import com.ambrose.saigonbyday.dto.request.SigninRequest;
import com.ambrose.saigonbyday.entities.User;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
  ResponseEntity<?> signup(User user);
  ResponseEntity<?> signin(SigninRequest signinRequest);

  ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest);
  //public ResponseEntity<?> checkPhoneResetToken(String phone);
  public ResponseEntity<?> checkEmail(String email);
  public String checkResetVerifyToken(String email, Long id);
  //public String checkResetVerifyTokenSms(String phone, Long id);
  public ResponseEntity<?> checkPassword(String email, String password);
  //public ResponseEntity<?> checkPasswordPhone(String phone, String password);
  public ResponseEntity<?> saveInfor(SignUp signUp);
  public ResponseEntity<?> saveInforPhone(SignUp signUp);
  //public ResponseEntity<?> checkPhone(String phone);
  public ResponseEntity<?> getUser(Long id);
}
