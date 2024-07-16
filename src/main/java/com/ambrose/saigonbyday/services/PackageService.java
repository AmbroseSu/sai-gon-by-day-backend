package com.ambrose.saigonbyday.services;


import com.ambrose.saigonbyday.dto.PackageDTO;
import com.ambrose.saigonbyday.dto.PackageRequestDTO;
import org.springframework.http.ResponseEntity;

public interface PackageService extends GenericService<PackageDTO>{

  ResponseEntity<?> save(PackageRequestDTO packageRequestDTO);
}
