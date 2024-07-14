package com.ambrose.saigonbyday.dto;

import com.ambrose.saigonbyday.entities.Order;
import com.ambrose.saigonbyday.entities.enums.Gender;
import com.ambrose.saigonbyday.entities.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import java.util.List;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
  private Long id;
  private String fullname;
  //private String login;
  private String address;
  private String email;
  //private String password;
  private String phone;
  private Gender gender;
  private Role role;
  private String DOB;
  //private List<Order> orders;
}
