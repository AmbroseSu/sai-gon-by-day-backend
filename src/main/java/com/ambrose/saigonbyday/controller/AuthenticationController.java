package com.ambrose.saigonbyday.controller;

import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.UpsertUserDTO;
import com.ambrose.saigonbyday.dto.request.SignUp;
import com.ambrose.saigonbyday.dto.request.SigninRequest;
import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.entities.VerificationToken;
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
import org.springframework.web.bind.annotation.RequestBody;
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
      try{
        User user = userRepository.findUserByEmail(email);
        publisher.publishEvent(new RegistrationCompleteEvent(user));
        return result;
      }catch (Exception ex)
      {
        return ResponseUtil.error(ex.getMessage(),"Wifi not connect", HttpStatus.BAD_REQUEST);
      }
    }
  }

  @PostMapping("/verifyEmail")
  public ResponseEntity<?>/*String*/ verifyEmail(@RequestParam("token") String token, @RequestParam("id") Long id){
    VerificationToken theToken = tokenRepository.findByToken(token);
    if(theToken == null){
      return ResponseUtil.error("Token not exist","Not Enable", HttpStatus.BAD_REQUEST);
    }
    if(theToken.getUser().isEnabled()) {
      return ResponseUtil.error("This account has already been verified, please, login",
          "Not Enable", HttpStatus.BAD_REQUEST);
      //return "This account has already been verified, please, login";
    }
    if(!id.equals(theToken.getUser().getId())){
      return ResponseUtil.error("Invalid verification token with user","Invalid token", HttpStatus.BAD_REQUEST);
    }
    String verificationResults = userService.validateToken(token, id);
    if(verificationResults.equalsIgnoreCase("Valid")){
      return ResponseUtil.getObject(null,HttpStatus.CREATED,"Email verified successfully. Now you can login to your account");
      //return "Email verified successfully. Now you can login to your account";
    }
    return ResponseUtil.error("Invalid verification token","Invalid token",HttpStatus.BAD_REQUEST);
    //return "Invalid verification token";
  }

  @PostMapping("/resetVerifyEmail")
  public ResponseEntity<?> resetVerifyEmail(@RequestParam("email") String email, @RequestParam("id") Long id){
    String checkverifytoken = authenticationService.checkResetVerifyToken(email,id);
    if(checkverifytoken.equalsIgnoreCase("true")){
      try{
        User user = userRepository.findUserByEmail(email);
        publisher.publishEvent(new RegistrationCompleteEvent(user));
        UpsertUserDTO result = (UpsertUserDTO) genericConverter.toDTO(user, UpsertUserDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.CREATED, "ok");
      }catch (Exception ex){
        return ResponseUtil.error(ex.getMessage(),"Wifi not connect", HttpStatus.BAD_REQUEST);
      }
    }else{
      return ResponseUtil.error("Send reset token false", "Reset token false", HttpStatus.BAD_REQUEST);
    }
  }
  @PostMapping("/saveInfor")
  public ResponseEntity<?> saveInfor(@RequestBody SignUp signUp){
    return authenticationService.saveInfor(signUp);
  }

  @PostMapping("/signin")
  public ResponseEntity<?> signin (@RequestBody SigninRequest signinRequest){
    return authenticationService.signin(signinRequest);
  }

}
