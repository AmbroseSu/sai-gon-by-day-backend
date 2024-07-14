package com.ambrose.saigonbyday.services;


import com.ambrose.saigonbyday.dto.PackageInDayDTO;
import org.springframework.http.ResponseEntity;

public interface PackageInDayService extends GenericService<PackageInDayDTO>{
  ResponseEntity<?> findAllSale(int page, int limit);
}
