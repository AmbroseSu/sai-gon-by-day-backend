package com.ambrose.saigonbyday.services;

import com.ambrose.saigonbyday.dto.PaymentHistoryDTO;
import com.ambrose.saigonbyday.entities.PaymentHistory;
import org.springframework.http.ResponseEntity;

public interface PaymentHistoryService extends GenericService<PaymentHistoryDTO>{

  ResponseEntity<?> findToTalMoneyWithMonth(int month, int year);
  ResponseEntity<?> getPaymentDashboard(int page, int limit);


}
