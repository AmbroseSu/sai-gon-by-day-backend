package com.ambrose.saigonbyday.services;


import com.ambrose.saigonbyday.dto.PackageInDayDTO;
import com.ambrose.saigonbyday.dto.request.PackageInDaySearchRequest;
import org.springframework.http.ResponseEntity;

public interface PackageInDayService extends GenericService<PackageInDayDTO>{
  ResponseEntity<?> findAllSale(int page, int limit);

  ResponseEntity<?> findAllWithSearchSortFilter(PackageInDaySearchRequest packageInDaySearchRequest);

}
