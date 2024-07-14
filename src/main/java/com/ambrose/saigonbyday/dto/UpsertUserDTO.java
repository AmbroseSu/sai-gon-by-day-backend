package com.ambrose.saigonbyday.dto;


import com.ambrose.saigonbyday.entities.enums.Gender;
import com.ambrose.saigonbyday.entities.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpsertUserDTO {
  private long id;
  private String fullname;
  //private String secondname;
  private String email;
  //private String password;
  //private String phone;
  private Gender gender;
  private Role role;
  private String FCMToken;
}
