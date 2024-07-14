package com.ambrose.saigonbyday.dto.request;

import com.ambrose.saigonbyday.entities.enums.Gender;
import lombok.Data;

@Data
public class SignUpGoogle {
  String email;
  String fullname;
  String phone;
  String address;
  Gender gender;
  String FCMToken;
}
