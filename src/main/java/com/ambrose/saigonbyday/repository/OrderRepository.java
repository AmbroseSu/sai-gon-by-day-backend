package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

  Order findById(Long id);




}
