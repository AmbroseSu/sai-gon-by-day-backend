package com.ambrose.saigonbyday.services.impl;

import com.ambrose.saigonbyday.config.CustomValidationException;
import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.DestinationDTO;
import com.ambrose.saigonbyday.dto.OrderDTO;
import com.ambrose.saigonbyday.entities.City;
import com.ambrose.saigonbyday.entities.Destination;
import com.ambrose.saigonbyday.entities.Order;
import com.ambrose.saigonbyday.entities.OrderDetails;
import com.ambrose.saigonbyday.entities.Package;
import com.ambrose.saigonbyday.entities.PackageInDay;
import com.ambrose.saigonbyday.entities.PaymentHistory;
import com.ambrose.saigonbyday.entities.enums.Status;
import com.ambrose.saigonbyday.repository.OrderDetailsRepository;
import com.ambrose.saigonbyday.repository.OrderRepository;
import com.ambrose.saigonbyday.repository.PackageInDayRepository;
import com.ambrose.saigonbyday.repository.PaymentHistoryRepository;
import com.ambrose.saigonbyday.repository.UserRepository;
import com.ambrose.saigonbyday.services.GenericService;
import com.ambrose.saigonbyday.services.OrderService;
import com.ambrose.saigonbyday.services.ServiceUtils;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final PackageInDayRepository packageInDayRepository;
  private final UserRepository userRepository;
  private final PaymentHistoryRepository paymentHistoryRepository;
  private final OrderRepository orderRepository;
  private final GenericConverter genericConverter;
  private final OrderDetailsRepository orderDetailsRepository;

  @Override
  public ResponseEntity<?> findById(Long id) {
    return null;
  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    return null;
  }

  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    return null;
  }

  @Override
  public ResponseEntity<?> save(OrderDTO orderDTO) {
    try {
      ServiceUtils.errors.clear();
      List<Long> requestPackageInDayIds = orderDTO.getPackageInDayIds();
      Long requestUserId = orderDTO.getUserId();
      Long requestPaymentHistoryId = orderDTO.getPaymentHistoryId();
      Float totalPrice = 0.0f;
      Order order;

      if (requestPackageInDayIds != null){
        ServiceUtils.validatePackageInDayIds(requestPackageInDayIds, packageInDayRepository);
      }
      if (requestUserId != null){
        ServiceUtils.validateUserIds(List.of(requestUserId), userRepository);
      } else {
        return ResponseUtil.error("Please login or register","Save Failed", HttpStatus.BAD_REQUEST);
      }
      if (requestPaymentHistoryId != null){
        ServiceUtils.validatePaymentHistoryIds(List.of(requestPaymentHistoryId), paymentHistoryRepository);
      }
      if (!ServiceUtils.errors.isEmpty()){
        throw new CustomValidationException(ServiceUtils.errors);
      }
      Order orderCheck = orderRepository.findOrderByUserId(orderDTO.getUserId());
      if (orderCheck != null){
        orderDTO.setId(orderCheck.getId());
      }

      if (orderDTO.getId() != null){
        //order = (Order) genericConverter.toEntity(orderDTO, Order.class);
        order = orderRepository.findById(orderDTO.getId());
        if (order == null) {
          return ResponseUtil.error("Order not found", "Save Failed", HttpStatus.NOT_FOUND);
        }
        List<OrderDetails> existingOrderDetails = orderDetailsRepository.findByOrderIdAndIs_status(orderDTO.getId(), Status.CART);
        Set<Long> existingPackageInDayIds = new HashSet<>();
        for (OrderDetails orderDetails : existingOrderDetails){
          totalPrice += orderDetails.getPrice();
          existingPackageInDayIds.add(orderDetails.getPackageInDay().getId());
        }
        for (Long packageInDayId : requestPackageInDayIds){
          if (!existingPackageInDayIds.contains(packageInDayId)){
            PackageInDay packageInDay = packageInDayRepository.findById(packageInDayId);
            if (packageInDay != null){
              totalPrice += packageInDay.getPrice();
            }
          }else {
            // Nếu PackageInDay đã tồn tại trong giỏ hàng, báo lỗi
            return ResponseUtil.error("PackageInDay ID " + packageInDayId + " is already in the cart", "Save Failed", HttpStatus.BAD_REQUEST);
          }

        }
        order.setTotalPrice(totalPrice);
        Date date = new Date();
        order.setDate(date.getTime());
        orderRepository.save(order);
        loadOrderDetailsFromListPackageInDayIds(requestPackageInDayIds, order.getId());


      } else {

        orderDTO.setStatus(true);
        for(Long packageInDayId : requestPackageInDayIds){
          PackageInDay packageInDay = packageInDayRepository.findById(packageInDayId);
          totalPrice += packageInDay.getPrice();
        }
        orderDTO.setTotalPrice(totalPrice);
        Date date = new Date();
        orderDTO.setDate(date.getTime());
        order = (Order) genericConverter.toEntity(orderDTO, Order.class);
        order.setUser(userRepository.findUserById(requestUserId));
        orderRepository.save(order);
        loadOrderDetailsFromListPackageInDayIds(requestPackageInDayIds, order.getId());


      }
      OrderDTO result = convertOrderToOrderDTO(order);
      if (orderDTO.getId() == null){
        result.setPackageInDayIds(requestPackageInDayIds);
      }
      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");



    }
    catch (Exception ex) {
      return ResponseUtil.error(ex.getMessage(),"Failed", HttpStatus.BAD_REQUEST);
    }
  }

  private void loadOrderDetailsFromListPackageInDayIds(List<Long> requestPackageInDayIds, Long orderId){
    if (requestPackageInDayIds != null && !requestPackageInDayIds.isEmpty()){
      for (Long packageInDayId : requestPackageInDayIds){
        PackageInDay packageInDay = packageInDayRepository.findById(packageInDayId);
        Order order = orderRepository.findById(orderId);
        if (packageInDay != null && order != null){
          boolean exists = orderDetailsRepository.existsByOrderIdAndPackageInDayId(orderId, packageInDayId);
          if(!exists){
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setOrder(order);
            orderDetails.setPackageInDay(packageInDay);
            orderDetails.setStatus(true);
            orderDetails.setIs_status(Status.CART);
            orderDetails.setPrice(packageInDay.getPrice());
            orderDetailsRepository.save(orderDetails);
          }
          }

      }
    }


  }

  private OrderDTO convertOrderToOrderDTO(Order order){
    OrderDTO newOrderDTO = (OrderDTO) genericConverter.toDTO(order, OrderDTO.class);
    List<PackageInDay> packageInDays = orderDetailsRepository.findPackageInDayByOrderId(order.getId());
    if (packageInDays == null){
      newOrderDTO.setPackageInDayIds(null);
    } else {
      List<Long> packageInDayIds = packageInDays.stream()
          .map(PackageInDay::getId)
          .toList();
      newOrderDTO.setPackageInDayIds(packageInDayIds);
    }
    if (order.getPaymentHistory() == null){
      newOrderDTO.setPaymentHistoryId(null);
    } else{
      PaymentHistory paymentHistory = orderRepository.findPaymentHistoryByPaymentHistoryId(order.getPaymentHistory().getId());
      newOrderDTO.setPaymentHistoryId(paymentHistory.getId());
    }
    return newOrderDTO;

  }



  @Override
  public ResponseEntity<?> changeStatus(Long id) {
    return null;
  }

  @Override
  public Boolean checkExist(Long id) {
    return null;
  }

  @Override
  @Transactional
  public ResponseEntity<?> confirmOrder(OrderDTO orderDTO) {
    try {

      List<Long> packageInDayIds = orderDTO.getPackageInDayIds();
      Order order = orderRepository.findOrderByUserId(orderDTO.getUserId());
      if (order == null) {
        return ResponseUtil.error("No order found to confirm", "Confirm Failed", HttpStatus.NOT_FOUND);
      }
      List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrderIdAndIs_status(order.getId(), Status.CART);
      Set<Long> packageInDayIdSet = new HashSet<>(packageInDayIds);
      Float totalPrice = 0.0f;
      for (OrderDetails orderDetails : orderDetailsList) {
        if (packageInDayIdSet.contains(orderDetails.getPackageInDay().getId())) {
          orderDetails.setIs_status(Status.CONFIRMED);
          orderDetailsRepository.save(orderDetails);
        }else {
          // Chỉ tính tổng giá trị của các PackageInDay có trạng thái là CART
          totalPrice += orderDetails.getPrice();
        }
      }
//      boolean allConfirmed = orderDetailsRepository.findByOrderIdAndIs_status(order.getId(), Status.CART).isEmpty();
//      if (allConfirmed) {
//        order.setIs_status(Status.CONFIRMED);
//      }
      order.setTotalPrice(totalPrice);
      orderRepository.save(order);
      OrderDTO result = (OrderDTO) genericConverter.toDTO(order, OrderDTO.class);
      result.setPackageInDayIds(packageInDayIds);
      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    } catch (Exception ex) {
      return ResponseUtil.error(ex.getMessage(), "Confirm Failed", HttpStatus.BAD_REQUEST);
    }
  }
}
