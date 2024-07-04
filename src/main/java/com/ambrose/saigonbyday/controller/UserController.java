package com.ambrose.saigonbyday.controller;

import com.ambrose.saigonbyday.entities.enums.Role;
import com.ambrose.saigonbyday.repository.UserRepository;
import com.ambrose.saigonbyday.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
public class UserController {
  //private final UserService userService;
  @Autowired
  private final UserService userService;
  @GetMapping("/get")
  //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<String> sayHello(){
    return ResponseEntity.ok("Hi User");
  }


  @GetMapping("/find-all-by-role")
  public ResponseEntity<?> findAllUserByRole(@RequestParam(name = "role") Role role, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return userService.findUserByRole(role, page, limit);
  }


}
