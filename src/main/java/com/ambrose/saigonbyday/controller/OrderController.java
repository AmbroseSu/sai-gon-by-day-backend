package com.ambrose.saigonbyday.controller;

import com.ambrose.saigonbyday.dto.OrderDTO;
import com.ambrose.saigonbyday.dto.PackageDTO;
import com.ambrose.saigonbyday.dto.PaymentHistoryDTO;
import com.ambrose.saigonbyday.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
@CrossOrigin
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

  @PostMapping("/find-all-sale-for-user/{userid}")
  public ResponseEntity<?> findAllPackageInDaySaleWithUserId(@PathVariable(name = "userid") Long id, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return orderService.findPackageInDaySalebyUserId(id, page, limit);
  }

  @PostMapping("/paid-order")
  public ResponseEntity<?> paidOrder(@RequestBody PaymentHistoryDTO paymentHistoryDTO){
    return orderService.paidOrder(paymentHistoryDTO);
  }
  @PostMapping("/find-all-paid-for-user/{userid}")
  public ResponseEntity<?> findAllPackageInDaySalePaidWithUserId(@PathVariable(name = "userid") Long id, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return orderService.findPackageInDaySalebyUserIdStatusPaid(id, page, limit);
  }

  @PostMapping("/find-all-confirm-for-user/{userid}")
  public ResponseEntity<?> findAllPackageInDaySaleConfirmWithUserId(@PathVariable(name = "userid") Long id, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return orderService.findByIsStatusConfirmWithUserId(id, page, limit);
  }

  @GetMapping("/total-money")
  public ResponseEntity<?> totalMoney(){
    return orderService.totalMoneySale();
  }

}
