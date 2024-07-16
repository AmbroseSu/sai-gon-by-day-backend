package com.ambrose.saigonbyday.controller;


import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.dto.PackageDTO;
import com.ambrose.saigonbyday.dto.PackageRequestDTO;
import com.ambrose.saigonbyday.services.PackageService;
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
@RequestMapping("/api/v1/package")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
@CrossOrigin
public class PackageController {

  @Autowired
  private PackageService packageService;

  @PostMapping("/save")
  public ResponseEntity<?> savePackage(@RequestBody PackageRequestDTO packageRequestDTO){
    return packageService.save(packageRequestDTO);
  }

  @PutMapping("update/{id}")
  public ResponseEntity<?> updatePackage(@RequestBody PackageDTO packageDTO, @PathVariable(name = "id") Long id){
    if (packageService.checkExist(id)){
      packageDTO.setId(id);
      return packageService.save(packageDTO);
    }
    return ResponseUtil.error("Not Found", "Gallery not exits", HttpStatus.NOT_FOUND);
  }

  @GetMapping("/find-id/{id}")
  public ResponseEntity<?> getPackageById(@PathVariable(name = "id") Long id){
    return packageService.findById(id);
  }

  @GetMapping("/find-all-true")
  //@PreAuthorize("hasAuthority('CUSTOMER')")
  public ResponseEntity<?> getAllPackageByStatusTrue(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return packageService.findAllByStatusTrue(page, limit);
  }

  @GetMapping("/find-all")
  public ResponseEntity<?> getAllPackage(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return packageService.findAll(page,limit);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> changeStatus(@PathVariable(name = "id") Long id){
    return packageService.changeStatus(id);
  }

}
