package com.ambrose.saigonbyday.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDashboardResponse {
  private String code;
  private String name;
  private int quantity;
  private float price;

}
