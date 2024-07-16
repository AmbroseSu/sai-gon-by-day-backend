package com.ambrose.saigonbyday.entities;

import com.ambrose.saigonbyday.entities.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_orderDetails")
public class OrderDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "orderId")
  private Order order;

  @ManyToOne
  @JoinColumn(name = "packageInDayId")
  private PackageInDay packageInDay;

  private Status is_status;
  private Boolean status;
  private Float price;
  @ManyToOne
  @JoinColumn(name = "paymentHistoryId")
  private PaymentHistory paymentHistory;
}
