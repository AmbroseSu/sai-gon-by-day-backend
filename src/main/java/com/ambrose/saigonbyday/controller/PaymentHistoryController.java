package com.ambrose.saigonbyday.controller;

import com.ambrose.saigonbyday.services.PackageInDayService;
import com.ambrose.saigonbyday.services.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment-history")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
@CrossOrigin
public class PaymentHistoryController {

  @Autowired
  private PaymentHistoryService paymentHistoryService;

  @PostMapping("/total-money-with-month")
  public ResponseEntity<?> totalMoneyWithMonth(@RequestParam(name = "month") int month, @RequestParam(name = "year") int year){
    return paymentHistoryService.findToTalMoneyWithMonth(month, year);
  }

  @GetMapping("/find-all-pay-dashboard")
  //@PreAuthorize("hasAuthority('CUSTOMER')")
  public ResponseEntity<?> paymentDashBoarch(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return paymentHistoryService.getPaymentDashboard(page, limit);
  }
}
