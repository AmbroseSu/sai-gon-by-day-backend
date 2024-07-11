package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, String> {

  PaymentHistory findById(Long id);


}
