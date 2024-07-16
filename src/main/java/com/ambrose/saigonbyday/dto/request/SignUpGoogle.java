package com.ambrose.saigonbyday.dto.request;

import com.ambrose.saigonbyday.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpGoogle {
  String email;
  String fullname;
  String phone;
  String address;
  Gender gender;
  String fcmtoken;
}
