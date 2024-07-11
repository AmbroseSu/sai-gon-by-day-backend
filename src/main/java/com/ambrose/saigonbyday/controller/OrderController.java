package com.ambrose.saigonbyday.controller;

import com.ambrose.saigonbyday.dto.OrderDTO;
import com.ambrose.saigonbyday.dto.PackageDTO;
import com.ambrose.saigonbyday.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/save")
  public ResponseEntity<?> saveOrder(@RequestBody OrderDTO orderDTO){
    return orderService.save(orderDTO);
  }

  @PostMapping("/confirm-order")
  public ResponseEntity<?> confirmOrder(@RequestBody OrderDTO orderDTO){
    return orderService.confirmOrder(orderDTO);
  }

}
