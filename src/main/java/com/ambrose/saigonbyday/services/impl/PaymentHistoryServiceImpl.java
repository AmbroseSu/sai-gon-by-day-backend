package com.ambrose.saigonbyday.services.impl;

import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.GalleryDTO;
import com.ambrose.saigonbyday.dto.PaymentHistoryDTO;
import com.ambrose.saigonbyday.dto.response.PaymentDashboardResponse;
import com.ambrose.saigonbyday.entities.Gallery;
import com.ambrose.saigonbyday.entities.OrderDetails;
import com.ambrose.saigonbyday.entities.Package;
import com.ambrose.saigonbyday.entities.PackageInDay;
import com.ambrose.saigonbyday.entities.PaymentHistory;
import com.ambrose.saigonbyday.entities.enums.Status;
import com.ambrose.saigonbyday.repository.OrderDetailsRepository;
import com.ambrose.saigonbyday.repository.PackageInDayRepository;
import com.ambrose.saigonbyday.repository.PackageRepository;
import com.ambrose.saigonbyday.repository.PaymentHistoryRepository;
import com.ambrose.saigonbyday.services.GenericService;
import com.ambrose.saigonbyday.services.PaymentHistoryService;
import com.ambrose.saigonbyday.services.ServiceUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

  private final PaymentHistoryRepository paymentHistoryRepository;
  private final GenericConverter genericConverter;
  private final OrderDetailsRepository orderDetailsRepository;
  private final PackageRepository packageRepository;
  private final PackageInDayRepository packageInDayRepository;

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
  public ResponseEntity<?> save(PaymentHistoryDTO paymentHistoryDTO) {
    return null;
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
  public ResponseEntity<?> findToTalMoneyWithMonth(int month, int year) {
    try {
      List<PaymentHistory> paymentHistories = paymentHistoryRepository.findAllByMonthAndYear(month, year);
      float total = 0.0f;

      for (PaymentHistory paymentHistory : paymentHistories) {
        List<OrderDetails> paidOrderDetails = orderDetailsRepository.findByPaymentHistoryIdAndIsStatus(paymentHistory.getId(), Status.PAID);
        for (OrderDetails orderDetail : paidOrderDetails) {
          total += orderDetail.getPrice();
        }
      }

      return ResponseUtil.getResult(total, HttpStatus.OK, "Successfully");
    }catch (Exception ex) {
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }

  }

  @Override
  public ResponseEntity<?> getPaymentDashboard(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);

    // Lấy danh sách các OrderDetails có trạng thái PAID và phân trang
    List<PackageInDay> packageInDays = packageInDayRepository.findAllBy(pageable);
    List<PaymentDashboardResponse> paymentDashboardResponses = new ArrayList<>();
    for (PackageInDay packageInDay : packageInDays){
      paymentDashboardResponses.add(converttoPaymentDashboardResponse(packageInDay));
    }

    // Nhóm các OrderDetails theo PackageInDay


    // Trả về đáp ứng HTTP
    return ResponseUtil.getCollection(paymentDashboardResponses,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        paymentHistoryRepository.countAllByStatusIsTrue());  // Số lượng tổng các bản ghi PAID
  }

  private PaymentDashboardResponse converttoPaymentDashboardResponse(PackageInDay packageInDay){
    PaymentDashboardResponse paymentDashboardResponse = new PaymentDashboardResponse();
    Package packagee = packageInDayRepository.findPackageByPackageInDayId(packageInDay.getId());
    List<OrderDetails> orderDetails = orderDetailsRepository.findByPackageInDayIdAndIs_status(
        packageInDay.getId(), Status.PAID);
    float totalprice = 0F;
    for(OrderDetails orderDetails1 : orderDetails){
      totalprice += orderDetails1.getPrice();
    }
    int quantity = (int)( totalprice/packageInDay.getPrice());
    paymentDashboardResponse.setCode(packageInDay.getCode());
    paymentDashboardResponse.setName(packagee.getName());
    paymentDashboardResponse.setPrice(totalprice);
    paymentDashboardResponse.setQuantity(quantity);
    return paymentDashboardResponse;
  }
}
