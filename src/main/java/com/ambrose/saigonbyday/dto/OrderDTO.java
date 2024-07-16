package com.ambrose.saigonbyday.dto;

import com.ambrose.saigonbyday.entities.OrderDetails;
import com.ambrose.saigonbyday.entities.PaymentHistory;
import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.entities.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

  private Long id;
  private Float totalPrice;
  private Long date;
  private Boolean status;
  private Long userId;
  private List<Long> packageInDayIds;

}
