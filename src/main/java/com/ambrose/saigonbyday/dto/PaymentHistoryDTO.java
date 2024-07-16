package com.ambrose.saigonbyday.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryDTO {
  private Long id;
  private Long userId;
  private String method;
  private Boolean status;
  private Long purchaseDate;
}
