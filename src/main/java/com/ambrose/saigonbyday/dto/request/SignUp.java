package com.ambrose.saigonbyday.dto.request;


import com.ambrose.saigonbyday.entities.enums.Gender;
import com.ambrose.saigonbyday.entities.enums.Role;
import lombok.Data;

@Data
public class SignUp {
  String email;
  String fullname;
  String phone;
  String password;
  String address;
  Gender gender;
  String FCMToken;
  //Role role;
}
