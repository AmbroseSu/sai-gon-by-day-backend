package com.ambrose.saigonbyday.services;

import com.ambrose.saigonbyday.dto.GalleryDTO;
import org.springframework.http.ResponseEntity;

public interface GalleryService extends GenericService<GalleryDTO>{

//  ResponseEntity<?> saveGallery(GalleryDTO galleryDTO);
//  ResponseEntity<?> findById(Long id);
//  public ResponseEntity<?> changeStatus(Long id);
//  public ResponseEntity<?> findAll(int page, int limit);
//  Boolean checkExist(Long id);
  ResponseEntity<?> findByDestinationId(Long id);
}
