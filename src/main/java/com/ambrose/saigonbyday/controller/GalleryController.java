package com.ambrose.saigonbyday.controller;


import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.dto.GalleryDTO;
import com.ambrose.saigonbyday.services.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gallery")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
@CrossOrigin
public class GalleryController {

  @Autowired
  private GalleryService galleryService;

  @PostMapping("/save")
  public ResponseEntity<?> saveGallery(@RequestBody GalleryDTO galleryDTO){
    return galleryService.save(galleryDTO);
  }

  @PutMapping ("update/{id}")
  public ResponseEntity<?> updateGellery(@RequestBody GalleryDTO galleryDTO, @PathVariable(name = "id") Long id){
    if (galleryService.checkExist(id)){
      galleryDTO.setId(id);
      return galleryService.save(galleryDTO);
    }
    return ResponseUtil.error("Not Found", "Gallery not exits", HttpStatus.NOT_FOUND);

  }

  @GetMapping("/find-id/{id}")
  //@PreAuthorize("hasAuthority('CUSTOMER')")
  public ResponseEntity<?> getGalleryById(@PathVariable(name = "id") Long id){
    return galleryService.findById(id);
  }

  @GetMapping("/find-by-destination-id/{id}")
  //@PreAuthorize("hasAuthority('CUSTOMER')")
  public ResponseEntity<?> getGalleryByDestinationId(@PathVariable(name = "id") Long id){
    return galleryService.findByDestinationId(id);
  }

  @GetMapping("/find-all-true")
  //@PreAuthorize("hasAuthority('CUSTOMER')")
  public ResponseEntity<?> getAllGalleryByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit){
    return galleryService.findAllByStatusTrue(page, limit);
  }

  @GetMapping("/find-all")
  //@PreAuthorize("hasAuthority('CUSTOMER')")
  public ResponseEntity<?> getAllGallery(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return galleryService.findAll(page, limit);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> changeStatus(@PathVariable(name = "id") Long id){
    return galleryService.changeStatus(id);
  }




}
