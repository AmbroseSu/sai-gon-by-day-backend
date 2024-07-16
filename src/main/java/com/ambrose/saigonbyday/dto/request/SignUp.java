package com.ambrose.saigonbyday.dto.request;


import com.ambrose.saigonbyday.entities.enums.Gender;
import com.ambrose.saigonbyday.entities.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUp {
  String email;
  String fullname;
  String phone;
  String password;
  String address;
  Gender gender;
  String fcmtoken;
  //Role role;
}
