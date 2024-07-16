package com.ambrose.saigonbyday.services.impl;

import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.GalleryDTO;
import com.ambrose.saigonbyday.dto.PackageInDaySaleDTO;
import com.ambrose.saigonbyday.dto.UserDTO;
import com.ambrose.saigonbyday.entities.Gallery;
import com.ambrose.saigonbyday.entities.PackageInDay;
import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.entities.VerificationToken;
import com.ambrose.saigonbyday.entities.enums.Role;
import com.ambrose.saigonbyday.repository.UserRepository;
import com.ambrose.saigonbyday.repository.VerificationTokenRepository;
import com.ambrose.saigonbyday.services.UserService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final VerificationTokenRepository tokenRepository;
  //private final TravelReferencesRepository travelReferencesRepository;
  //private final UserTravelReferencesRepository userTravelReferencesRepository;
  private final GenericConverter genericConverter;
  //private final OtpSmsRepository otpSmsRepository;

  @Override
  public UserDetailsService userDetailsService(){
    return new UserDetailsService() {
      @Override
      public UserDetails loadUserByUsername(String username){
        return userRepository.findByLogin(username)
            //.map(UserSignupDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
      }
    };
  }

  @Override
  public void saveUserVerificationToken(User theUser, String token) {
    var verificationToken = new VerificationToken(token, theUser);
    tokenRepository.save(verificationToken);
  }


  @Override
  public String validateToken(String theToken, Long id) {
    VerificationToken token = tokenRepository.findByToken(theToken);
    if(token == null || !token.getUser().getId().equals(id)){
      return "Invalid verification token";
    }
    User user = token.getUser();
    Calendar calendar = Calendar.getInstance();
    Long to = token.getExpirationTime().getTime();
    long no = calendar.getTime().getTime();
    //Long time = (token.getTokenExpirationTime().getTime() - calendar.getTime().getTime());
    Long time = (to - no);
    if( time <= 0){
      tokenRepository.delete(token);
      return "Token already expired";
    }
    user.setEnabled(true);
    userRepository.save(user);
    tokenRepository.delete(token);
    return "Valid";
  }

  @Override
  public ResponseEntity<?> findUserByRole(Role role, int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<User> userList = userRepository.findByRole(role, pageable);
    List<UserDTO> result = new ArrayList<>();
    convertListUserToListUserDTO(userList, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        userRepository.countAllByIsEnabledIsTrue());

  }

  @Override
  public ResponseEntity<?> findById(Long id) {
    try{
      User user = userRepository.findUserById(id);
      if (user != null){
        UserDTO result = (UserDTO) genericConverter.toDTO(user, UserDTO.class);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");

      } else {
        return ResponseUtil.error("User not found", "Cannot find User", HttpStatus.NOT_FOUND);
      }

    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<User> entities = userRepository.findAllBy(pageable);
    List<UserDTO> result = new ArrayList<>();

    convertListUserToListUserDTO(entities, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        userRepository.countAllByIsEnabledIsTrue());
  }

  private void convertListUserToListUserDTO(List<User> users, List<UserDTO> result){
    for(User user : users){
      UserDTO newUserDTO = (UserDTO) genericConverter.toDTO(user, UserDTO.class);
      result.add(newUserDTO);
    }
  }


}
