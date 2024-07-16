package com.ambrose.saigonbyday.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_payment")
public class PaymentHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String method;
  private Boolean status;
  private Date purchaseDate;

  @OneToMany(mappedBy = "paymentHistory", cascade = CascadeType.ALL)
  private List<OrderDetails> orderDetails;
}
