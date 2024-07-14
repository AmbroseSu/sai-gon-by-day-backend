package com.ambrose.saigonbyday.services;

import com.ambrose.saigonbyday.dto.OrderDTO;
import org.springframework.http.ResponseEntity;

public interface OrderService extends GenericService<OrderDTO>{

  ResponseEntity<?> confirmOrder(OrderDTO orderDTO);

  ResponseEntity<?> findPackageInDaySalebyUserId(Long userId, int page, int limit);

}
