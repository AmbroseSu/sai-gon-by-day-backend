package com.ambrose.saigonbyday.services.impl;


import com.ambrose.saigonbyday.config.CustomValidationException;
import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.PackageDTO;
import com.ambrose.saigonbyday.entities.Destination;
import com.ambrose.saigonbyday.entities.Package;
import com.ambrose.saigonbyday.entities.PackageInDestination;
import com.ambrose.saigonbyday.repository.DestinationRepository;
import com.ambrose.saigonbyday.repository.PackageInDayRepository;
import com.ambrose.saigonbyday.repository.PackageInDestinationRepository;
import com.ambrose.saigonbyday.repository.PackageRepository;
import com.ambrose.saigonbyday.services.PackageService;
import com.ambrose.saigonbyday.services.ServiceUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {

  private final PackageRepository packageRepository;
  private final PackageInDayRepository packageInDayRepository;
  private final PackageInDestinationRepository packageInDestinationRepository;
  private final GenericConverter genericConverter;
  private final DestinationRepository destinationRepository;

  @Override
  public ResponseEntity<?> findById(Long id) {
    try{
      Package packagee = packageRepository.findByStatusIsTrueAndId(id);
      if (packagee != null){
        PackageDTO result = convertPackageToPackageDTO(packagee);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
      } else {
        return ResponseUtil.error("Package not found", "Cannot find Package", HttpStatus.NOT_FOUND);
      }
    } catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    try{
      Pageable pageable = PageRequest.of(page - 1, limit);
      List<Package> packages = packageRepository.findAllByStatusIsTrue(pageable);
      List<PackageDTO> result = new ArrayList<>();

      convertListPackageToListPackageDTO(packages, result);

      return ResponseUtil.getCollection(result,
          HttpStatus.OK,
          "Fetched successfully",
          page,
          limit,
          packageRepository.countAllByStatusIsTrue());

    } catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Package> entities = packageRepository.findAllBy(pageable);
    List<PackageDTO> result = new ArrayList<>();

    convertListPackageToListPackageDTO(entities, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        packageRepository.countAllByStatusIsTrue());
  }

  private void convertListPackageToListPackageDTO(List<Package> packages, List<PackageDTO> result){
    for(Package packagee : packages){
      PackageDTO newPackageDTO = convertPackageToPackageDTO(packagee);
      result.add(newPackageDTO);
    }
  }

  @Override
  public ResponseEntity<?> save(PackageDTO packageDTO) {
    try{

      ServiceUtils.errors.clear();
      List<Long> requestPackageInDayIds = packageDTO.getPackageInDayIds();
      List<Long> requestDestinationIds = packageDTO.getDestinationIds();

      Package packagee;

      if (requestPackageInDayIds != null){
        ServiceUtils.validatePackageInDayIds(requestPackageInDayIds, packageInDayRepository);
      }
      if (requestDestinationIds != null){
        ServiceUtils.validatePackageInDestinationIds(requestDestinationIds, packageInDestinationRepository);
      }
      if (!ServiceUtils.errors.isEmpty()){
        throw new CustomValidationException(ServiceUtils.errors);
      }

      if (packageDTO.getId() != null){
        Package oldEntity = packageRepository.findById(packageDTO.getId());
        Package tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
        packagee = (Package) genericConverter.toEntity(packageDTO, PackageDTO.class);
        packagee = ServiceUtils.fillMissingAttribute(packagee, tempOldEntity);
        packageInDestinationRepository.deleteAllByPackageeId(packageDTO.getId());
        loadPackageInDestinationFromListDestinationIds(requestDestinationIds, packagee.getId());

        packageRepository.save(packagee);

      } else {

        packageDTO.setStatus(true);
        packagee = (Package) genericConverter.toEntity(packageDTO, Package.class);
        packageRepository.save(packagee);
        loadPackageInDestinationFromListDestinationIds(requestDestinationIds, packagee.getId());

      }
      PackageDTO result = convertPackageToPackageDTO(packagee);
      if (packageDTO.getId() == null){
        result.setDestinationIds(requestDestinationIds);
      }
      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");


    } catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(),"Failed", HttpStatus.BAD_REQUEST);
    }
  }

  private PackageDTO convertPackageToPackageDTO(Package packagee){
    PackageDTO newPackageDTO = (PackageDTO) genericConverter.toDTO(packagee, PackageDTO.class);
    List<Destination> destinations = packageInDestinationRepository.findDestinationsByPackageId(packagee.getId());

    if (destinations == null){
      newPackageDTO.setDestinationIds(null);
    } else {
      List<Long> destinationIds = destinations.stream()
          .map(Destination::getId)
          .toList();
      newPackageDTO.setDestinationIds(destinationIds);
    }
    return newPackageDTO;


  }

  private void loadPackageInDestinationFromListDestinationIds(List<Long> requestDestinationIds, Long packageId){
    if (requestDestinationIds != null && !requestDestinationIds.isEmpty()){
      for (Long destinationId : requestDestinationIds){
        Destination destination = destinationRepository.findById(destinationId);
        Package packagee = packageRepository.findById(packageId);
        if(destination != null && packagee != null){
          PackageInDestination pid = new PackageInDestination();
          pid.setPackagee(packagee);
          pid.setDestination(destination);
          packageInDestinationRepository.save(pid);
        }
      }
    }
  }

  @Override
  public ResponseEntity<?> changeStatus(Long id) {
    try{
      Package packagee = packageRepository.findById(id);
      if(packagee != null){
        if (packagee.getStatus()){
          packagee.setStatus(false);
        } else {
          packagee.setStatus(true);
        }
        packageRepository.save(packagee);
        return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
      } else {
        return ResponseUtil.error("Package not found", "Cannot change status of non-existing Package", HttpStatus.NOT_FOUND);
      }
    } catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public Boolean checkExist(Long id) {
    Package packagee = packageRepository.findById(id);
    return packagee != null;
  }
}
