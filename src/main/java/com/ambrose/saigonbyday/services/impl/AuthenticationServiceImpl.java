package com.ambrose.saigonbyday.services.impl;


import com.ambrose.saigonbyday.config.ConstraintViolationExceptionHandler;
import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.UpsertUserDTO;
import com.ambrose.saigonbyday.dto.UserDTO;
import com.ambrose.saigonbyday.dto.request.RefreshTokenRequest;
import com.ambrose.saigonbyday.dto.request.SignUp;
import com.ambrose.saigonbyday.dto.request.SigninRequest;
import com.ambrose.saigonbyday.dto.response.JwtAuthenticationResponse;
import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.entities.VerificationToken;
import com.ambrose.saigonbyday.entities.enums.Role;
import com.ambrose.saigonbyday.repository.UserRepository;
import com.ambrose.saigonbyday.repository.VerificationTokenRepository;
import com.ambrose.saigonbyday.services.AuthenticationService;
import com.ambrose.saigonbyday.services.JWTService;
import com.ambrose.saigonbyday.services.UserService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.validation.ConstraintViolationException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;
  private final GenericConverter genericConverter;
  private final VerificationTokenRepository verificationTokenRepository;
  //private final OtpSmsRepository otpSmsRepository;
  //private final TwilioConfig twilioConfig;

  public ResponseEntity<?> signup(User user){
    try {
      // check if user already exists
      if (userRepository.existsByEmail(user.getEmail())) {
        return ResponseUtil.error("Email is already in use","Sign up failed", HttpStatus.BAD_REQUEST);
      }

      //user.setEmail(user.getEmail());
      user.setId(user.getId());
      user.setFullname(user.getFullname());
      //user.setSecondname(user.getSecondname());
      user.setRole(Role.CUSTOMER);
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      UpsertUserDTO result = (UpsertUserDTO) genericConverter.toDTO(user, UpsertUserDTO.class);
      userRepository.save(user);
      return ResponseUtil.getObject(result, HttpStatus.CREATED, "ok");
    } catch (ConstraintViolationException e) {
      return ConstraintViolationExceptionHandler.handleConstraintViolation(e);
    }


    //return ResponseUtil.getObject(user, HttpStatus.OK, "Sign up successfully");

  }

  public ResponseEntity<?> checkEmail(String email){
    try {
      if(userRepository.existsByEmail(email)){
        return ResponseUtil.error("Email is already in use","Failed", HttpStatus.BAD_REQUEST);
      }
      String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(email);

      if (!matcher.matches()) {
        return ResponseUtil.error("Form email not true", "False", HttpStatus.BAD_REQUEST);
      }
      User user = new User();
      user.setEmail(email);
      user.setLogin(email);
      userRepository.save(user);
      UpsertUserDTO result = (UpsertUserDTO) genericConverter.toDTO(user, UpsertUserDTO.class);

      return ResponseUtil.getObject(result, HttpStatus.CREATED, "ok");
    }catch (ConstraintViolationException e) {
      return ConstraintViolationExceptionHandler.handleConstraintViolation(e);
    }
  }

  public String checkResetVerifyToken(String email, Long id){
    try{
      User user = userRepository.findUserByEmail(email);
      VerificationToken verificationToken = verificationTokenRepository.findByUserId(id);
      if(user.getId().equals(id) && !user.isEnabled() && verificationToken == null){
        return "True";
      }else{
        return "False";
      }
    }catch (ConstraintViolationException e) {
      return e.toString();
    }
  }

  public ResponseEntity<?> signin(SigninRequest signinRequest){
    try{
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getLogin(),signinRequest.getPassword()));
    }catch (Exception e){
      return ResponseUtil.error("Email or Password not exist", "Login False",HttpStatus.BAD_REQUEST);
    }


    var user = userRepository.findByLogin(signinRequest.getLogin()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    var jwt = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

    JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
    UserDTO userDTO = convertUserToUserDTO(user);

    jwtAuthenticationResponse.setUserDTO(userDTO);
    jwtAuthenticationResponse.setToken(jwt);
    jwtAuthenticationResponse.setRefreshToken(refreshToken);
    return ResponseUtil.getObject(jwtAuthenticationResponse, HttpStatus.OK, "Sign in successfully");

  }


  public ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest){
    String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
    User user = userRepository.findByLogin(userEmail).orElseThrow();
    if(jwtService.isTokenValid(refreshTokenRequest.getToken(),user)){
      var jwt = jwtService.generateToken(user);

      JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
      jwtAuthenticationResponse.setToken(jwt);
      jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
      return ResponseUtil.getObject(jwtAuthenticationResponse, HttpStatus.OK, "Token sent successfully");
    }
    return null;
  }

  private UserDTO convertUserToUserDTO(User entity) {
    UserDTO newUserDTO = (UserDTO) genericConverter.toDTO(entity, UserDTO.class);
    return newUserDTO;
  }

  public ResponseEntity<?> checkPassword(String email, String password){
    try{
      User user = userRepository.findUserByEmail(email);
      if(user == null){
        return ResponseUtil.error("Email not exist","Failed", HttpStatus.BAD_REQUEST);
      }
      //check isenable
      if(!user.isEnabled()){
        return ResponseUtil.error("Please verify email before send password", "False", HttpStatus.BAD_REQUEST);
      }
      String regex = "^(?=.*[A-Z])(?=.*[!@#$%^&*()])(?=.*[0-9]).{8,32}$";

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(password);

      if (!matcher.matches()) {
        return ResponseUtil.error("Password Invalid", "False", HttpStatus.BAD_REQUEST);
      }
      user.setPassword(passwordEncoder.encode(password));
      UpsertUserDTO result = (UpsertUserDTO) genericConverter.toDTO(user, UpsertUserDTO.class);
      userRepository.save(user);
      return ResponseUtil.getObject(result, HttpStatus.CREATED, "ok");
    }catch (ConstraintViolationException e) {
      return ConstraintViolationExceptionHandler.handleConstraintViolation(e);
    }
  }

  public ResponseEntity<?> checkPasswordPhone(String phone, String password){
    try{
      User user = userRepository.findUserByPhone(phone);
      if(user == null){
        return ResponseUtil.error("Email not exist","Failed", HttpStatus.BAD_REQUEST);
      }
      //check isenable
      if(!user.isEnabled()){
        return ResponseUtil.error("Please verify email before send password", "False", HttpStatus.BAD_REQUEST);
      }
      String regex = "^(?=.*[A-Z])(?=.*[!@#$%^&*()])(?=.*[0-9]).{8,32}$";

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(password);

      if (!matcher.matches()) {
        return ResponseUtil.error("Password Invalid", "False", HttpStatus.BAD_REQUEST);
      }
      user.setPassword(passwordEncoder.encode(password));
      UpsertUserDTO result = (UpsertUserDTO) genericConverter.toDTO(user, UpsertUserDTO.class);
      userRepository.save(user);
      return ResponseUtil.getObject(result, HttpStatus.CREATED, "ok");
    }catch (ConstraintViolationException e) {
      return ConstraintViolationExceptionHandler.handleConstraintViolation(e);
    }
  }

  public ResponseEntity<?> saveInfor(SignUp signUp){
    try{
      User user = userRepository.findUserByEmail(signUp.getEmail());
      if(user == null){
        return ResponseUtil.error("Email not exist","Failed", HttpStatus.BAD_REQUEST);
      }
      //check isenable
      if(!user.isEnabled()){
        return ResponseUtil.error("Please verify email before send password", "False", HttpStatus.BAD_REQUEST);
      }
      user.setFullname(signUp.getFullname());
      //user.setCountry(signUp.getCountry());

      String phone = signUp.getPhone().substring(2);
      String regex = "^(\\+?\\d{1,4})?[-.\\s]?\\(?(\\d{2,3})\\)?[-.\\s]?\\d{3,4}[-.\\s]?\\d{3,4}$";

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(phone);

      if (!matcher.matches()) {
        return ResponseUtil.error("Phone number must be 10 number", "False", HttpStatus.BAD_REQUEST);
      }
      user.setPhone(signUp.getPhone());
      //user.setGender(signUp.getGender());
      user.setRole(Role.CUSTOMER);
      UpsertUserDTO result = (UpsertUserDTO) genericConverter.toDTO(user, UpsertUserDTO.class);
      userRepository.save(user);


      return ResponseUtil.getObject(result, HttpStatus.CREATED, "ok");
    }catch (ConstraintViolationException e) {
      return ConstraintViolationExceptionHandler.handleConstraintViolation(e);
    }
  }

  public ResponseEntity<?> saveInforPhone(SignUp signUp){
    try{
      User user = userRepository.findUserByPhone(signUp.getPhone());
      if(user == null){
        return ResponseUtil.error("Phone Number not exist","Failed", HttpStatus.BAD_REQUEST);
      }
      //check isenable
      if(!user.isEnabled()){
        return ResponseUtil.error("Please verify phone before send password", "False", HttpStatus.BAD_REQUEST);
      }
      user.setFullname(signUp.getFullname());
      //user.setCountry(signUp.getCountry());

      String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(signUp.getEmail());

      if (!matcher.matches()) {
        return ResponseUtil.error("Form email not true", "False", HttpStatus.BAD_REQUEST);
      }
      //user.setGender(signUp.getGender());
      user.setRole(Role.CUSTOMER);
      user.setEmail(signUp.getEmail());
      UpsertUserDTO result = (UpsertUserDTO) genericConverter.toDTO(user, UpsertUserDTO.class);
      userRepository.save(user);


      return ResponseUtil.getObject(result, HttpStatus.CREATED, "ok");
    }catch (ConstraintViolationException e) {
      return ConstraintViolationExceptionHandler.handleConstraintViolation(e);
    }
  }





  public ResponseEntity<?> getUser(Long id){
    try{
      User user = userRepository.findUserById(id);
      if(user == null){
        return ResponseUtil.error("User not exist", "Get User False", HttpStatus.BAD_REQUEST);
      }
      UpsertUserDTO result = (UpsertUserDTO) genericConverter.toDTO(user, UpsertUserDTO.class);
      return ResponseUtil.getObject(result,HttpStatus.OK,"Get User Successfully");
    }catch (ConstraintViolationException e){
      return ResponseUtil.error(e.toString(),e.getMessage(),HttpStatus.BAD_REQUEST);
    }


  }



  private String generateOTP() {
    return new DecimalFormat("00000")
        .format(new Random().nextInt(99999));
  }
}
