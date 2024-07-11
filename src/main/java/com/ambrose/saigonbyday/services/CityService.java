package com.ambrose.saigonbyday.services;


import com.ambrose.saigonbyday.dto.CityDTO;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface CityService extends GenericService<CityDTO>{


//   ResponseEntity<?> saveCity(CityDTO cityDTO);
//   ResponseEntity<?> findById(Long id);
//   public ResponseEntity<?> changeStatus(Long id);
//   public ResponseEntity<?> findAll(int page, int limit);
//   Boolean checkExist(Long id);

  void saveCitiesFromExcel(String filePath) throws IOException;

}
