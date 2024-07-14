package com.ambrose.saigonbyday.controller;


import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.dto.PackageInDayDTO;
import com.ambrose.saigonbyday.dto.request.PackageInDaySearchRequest;
import com.ambrose.saigonbyday.services.PackageInDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/package-in-day")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
public class PackageInDayController {

  @Autowired
  private PackageInDayService packageInDayService;

  @PostMapping("/save")
  public ResponseEntity<?> savePackageInDay(@RequestBody PackageInDayDTO packageInDayDTO){
    return packageInDayService.save(packageInDayDTO);
  }

  @PutMapping("update/{id}")
  public ResponseEntity<?> updatePackageInDay(@RequestBody PackageInDayDTO packageInDayDTO, @PathVariable(name = "id") Long id){
    if (packageInDayService.checkExist(id)){
      packageInDayDTO.setId(id);
      return packageInDayService.save(packageInDayDTO);
    }
    return ResponseUtil.error("Not Found", "Package In Day not exits", HttpStatus.NOT_FOUND);
  }

  @GetMapping("/find-id/{id}")
  public ResponseEntity<?> getPackageInDayById(@PathVariable(name = "id") Long id){
    return packageInDayService.findById(id);
  }

  @GetMapping("/find-all-true")
  //@PreAuthorize("hasAuthority('CUSTOMER')")
  public ResponseEntity<?> getAllPackageInDayByStatusTrue(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return packageInDayService.findAllByStatusTrue(page, limit);
  }

  @GetMapping("/find-all")
  public ResponseEntity<?> getAllPackageInDay(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return packageInDayService.findAll(page,limit);
  }


  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> changeStatus(@PathVariable(name = "id") Long id){
    return packageInDayService.changeStatus(id);
  }
  @GetMapping("/find-all-sale")
  public ResponseEntity<?> getAllPackageInDaySale(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit){
    return packageInDayService.findAllSale(page,limit);
  }

  @PostMapping("/find-all-search")
  public ResponseEntity<?> getAllPackageInDaySearch(@RequestBody PackageInDaySearchRequest packageInDaySearchRequest){
    return packageInDayService.findAllWithSearchSortFilter(packageInDaySearchRequest);
  }

}
