package com.ambrose.saigonbyday.services;


import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.entities.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService {
  UserDetailsService userDetailsService();

  void saveUserVerificationToken(User theUser, String verificationToken);
  //void saveUserVerificationTokenSMS(User theUser, String token);

  String validateToken(String theToken, Long id);
  //String validateTokenSms(String theToken, Long id);

  ResponseEntity<?> findUserByRole(Role role, int page, int limit);

}
