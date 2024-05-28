package com.ambrose.saigonbyday.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Calendar;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class VerificationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String token;
  private Date expirationTime;
  private static final int EXPRATION_TIME = 1;

  @OneToOne
  @JoinColumn(name = "tbl_user_id")
  private User user;

  public VerificationToken(String token, User user) {
    super();
    this.token = token;
    this.user = user;
    this.expirationTime = this.getTokenExpirationTime();
  }

  public VerificationToken(String token) {
    super();
    this.token = token;
    this.expirationTime = this.getTokenExpirationTime();
  }

  public Date getTokenExpirationTime() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(new Date().getTime());
    calendar.add(Calendar.MINUTE, EXPRATION_TIME);
    Date date = new Date(calendar.getTime().getTime());
    return date;
  }
}
