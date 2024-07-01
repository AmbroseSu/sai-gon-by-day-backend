package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.Order;
import com.ambrose.saigonbyday.entities.OrderDetails;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, String> {
  List<OrderDetails> findAllBy(Pageable pageable);

  OrderDetails findById(Long id);

  @Transactional
  Integer deleteAllByPackageInDayId(Long packageInDayId);

  @Query("SELECT or FROM Order or "
      + "JOIN OrderDetails ord ON or.id = ord.order.id "
      + "WHERE ord.packageInDay.id = :packageInDayId")
  List<Order> findOrdersByPackageInDayId(@Param("packageInDayId") Long packageInDayId);
}
