package com.ambrose.saigonbyday.controller;

import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.event.RegistrationCompleteEvent;
import com.ambrose.saigonbyday.repository.UserRepository;
import com.ambrose.saigonbyday.repository.VerificationTokenRepository;
import com.ambrose.saigonbyday.services.AuthenticationService;
import com.ambrose.saigonbyday.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;
  private final ApplicationEventPublisher publisher;
  private final VerificationTokenRepository tokenRepository;
  private final UserService userService;
  private final UserRepository userRepository;
  private final GenericConverter genericConverter;

  @PostMapping("/checkemail")
  public ResponseEntity<?> checkEmail(@RequestParam("email") String email){
    ResponseEntity<?> result = authenticationService.checkEmail(email);
    if(result.getStatusCode()== HttpStatus.BAD_REQUEST){
      return ResponseUtil.error("Email is already in use","Sign up failed", HttpStatus.BAD_REQUEST);
    }else{
      User user = userRepository.findUserByEmail(email);
      publisher.publishEvent(new RegistrationCompleteEvent(user));
      return result;
    }
  }

}
