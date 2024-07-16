package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.OrderDetails;
import com.ambrose.saigonbyday.entities.PaymentHistory;
import com.ambrose.saigonbyday.entities.enums.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, String> {

  PaymentHistory findById(Long id);

  @Query("SELECT p FROM PaymentHistory p WHERE MONTH(p.purchaseDate) = :month AND YEAR(p.purchaseDate) = :year")
  List<PaymentHistory> findAllByMonthAndYear(int month, int year);

  Long countAllByStatusIsTrue();



}
