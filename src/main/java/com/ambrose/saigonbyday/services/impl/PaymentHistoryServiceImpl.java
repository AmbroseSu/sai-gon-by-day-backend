package com.ambrose.saigonbyday.services.impl;

import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.GalleryDTO;
import com.ambrose.saigonbyday.dto.PaymentHistoryDTO;
import com.ambrose.saigonbyday.entities.Gallery;
import com.ambrose.saigonbyday.entities.PaymentHistory;
import com.ambrose.saigonbyday.repository.PaymentHistoryRepository;
import com.ambrose.saigonbyday.services.GenericService;
import com.ambrose.saigonbyday.services.PaymentHistoryService;
import com.ambrose.saigonbyday.services.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

  private final PaymentHistoryRepository paymentHistoryRepository;
  private final GenericConverter genericConverter;

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
}
