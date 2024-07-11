package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.Order;
import com.ambrose.saigonbyday.entities.OrderDetails;
import com.ambrose.saigonbyday.entities.PackageInDay;
import com.ambrose.saigonbyday.entities.enums.Status;
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

  @Transactional
  Integer deleteAllByOrderId(Long orderId);

  OrderDetails findById(Long id);
  boolean existsByOrderIdAndPackageInDayId(Long orderId, Long packageInDayId);

  @Transactional
  Integer deleteAllByPackageInDayId(Long packageInDayId);

  @Query("SELECT or FROM Order or "
      + "JOIN OrderDetails ord ON or.id = ord.order.id "
      + "WHERE ord.packageInDay.id = :packageInDayId")
  List<Order> findOrdersByPackageInDayId(@Param("packageInDayId") Long packageInDayId);

  @Query("SELECT pid FROM PackageInDay pid "
      + "JOIN OrderDetails od ON pid.id = od.packageInDay.id "
      + "WHERE od.order.id = :orderId")
  List<PackageInDay> findPackageInDayByOrderId(@Param("orderId") Long orderId);

  @Query("SELECT od FROM OrderDetails od WHERE od.order.id = :orderId AND od.is_status = :status")
  List<OrderDetails> findByOrderIdAndIs_status(@Param("orderId") Long orderId, @Param("status") Status status);

}
