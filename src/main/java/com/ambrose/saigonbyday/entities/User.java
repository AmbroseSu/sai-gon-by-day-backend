package com.ambrose.saigonbyday.entities;

import com.ambrose.saigonbyday.entities.enums.Gender;
import com.ambrose.saigonbyday.entities.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@Table(name = "tbl_user")
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String fullname;
  private String login;
  private String address;
  private String email;
  private String password;
  private String phone;
  private Gender gender;
  private Role role;
  private String DOB;
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Order> orders;
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Service> services;

  private boolean isEnabled = false;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getUsername() {
    return login;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isEnabled;
  }

}
