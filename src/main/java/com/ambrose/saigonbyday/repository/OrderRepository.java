package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.Order;
import com.ambrose.saigonbyday.entities.PaymentHistory;
import com.ambrose.saigonbyday.entities.enums.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

  Order findById(Long id);

  @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
  Order findOrderByUserId(@Param("userId") Long userId);



}
