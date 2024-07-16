package com.ambrose.saigonbyday.services.impl;


import com.ambrose.saigonbyday.config.CustomValidationException;
import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.PackageDTO;
import com.ambrose.saigonbyday.dto.PackageRequestDTO;
import com.ambrose.saigonbyday.dto.ServiceDestinationDTO;
import com.ambrose.saigonbyday.dto.response.PackageServiceResponse;
import com.ambrose.saigonbyday.entities.Destination;
import com.ambrose.saigonbyday.entities.Package;
import com.ambrose.saigonbyday.entities.PackageInDestination;
import com.ambrose.saigonbyday.entities.ServiceInPackage;
import com.ambrose.saigonbyday.entities.Servicee;
import com.ambrose.saigonbyday.repository.DestinationRepository;
import com.ambrose.saigonbyday.repository.PackageInDayRepository;
import com.ambrose.saigonbyday.repository.PackageInDestinationRepository;
import com.ambrose.saigonbyday.repository.PackageRepository;
import com.ambrose.saigonbyday.repository.ServiceInPackageRepository;
import com.ambrose.saigonbyday.repository.ServiceRepository;
import com.ambrose.saigonbyday.services.PackageService;
import com.ambrose.saigonbyday.services.ServiceUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
  private final ServiceRepository serviceRepository;
  private final ServiceInPackageRepository serviceInPackageRepository;

  @Override
  public ResponseEntity<?> findById(Long id) {
//    try{
//      Package packagee = packageRepository.findByStatusIsTrueAndId(id);
//      if (packagee != null){
//        PackageDTO result = convertPackageToPackageDTO(packagee);
//        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
//      } else {
//        return ResponseUtil.error("Package not found", "Cannot find Package", HttpStatus.NOT_FOUND);
//      }
//    } catch (Exception ex){
//      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
//    }
    try {
      Package packagee = packageRepository.findByStatusIsTrueAndId(id);
      if (packagee != null) {
        // Chuyển đổi Package thành PackageDTO
        PackageDTO packageDTO = convertPackageToPackageDTO(packagee);

        // Lấy danh sách PackageInDestination
        List<PackageInDestination> packageInDestinations = packageInDestinationRepository.findAllByPackageeId(packagee.getId());

        // Lấy danh sách ServiceDestinationDTO từ ServiceInPackage
        List<ServiceDestinationDTO> serviceDestinationDTOS = new ArrayList<>();
        for (PackageInDestination packageInDestination : packageInDestinations) {
          List<ServiceInPackage> serviceInPackages = serviceInPackageRepository.findByPackageInDestinationId(packageInDestination.getId());
          for (ServiceInPackage serviceInPackage : serviceInPackages) {
            ServiceDestinationDTO serviceDestinationDTO = new ServiceDestinationDTO();
            serviceDestinationDTO.setId(serviceInPackage.getServicee().getId());
            serviceDestinationDTO.setStartTime(packageInDestination.getStartTime());
            serviceDestinationDTO.setEndTime(packageInDestination.getEndTime());
            serviceDestinationDTO.setTransportation(packageInDestination.getTransportation());
            serviceDestinationDTOS.add(serviceDestinationDTO);
          }
        }

        // Tạo đối tượng PackageRequestDTO
        PackageRequestDTO result = new PackageRequestDTO(packageDTO, serviceDestinationDTOS);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
      } else {
        return ResponseUtil.error("Package not found", "Cannot find Package", HttpStatus.NOT_FOUND);
      }
    } catch (Exception ex) {
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

  @Override
  public ResponseEntity<?> save(PackageDTO packageDTO) {
    return null;
  }

  private void convertListPackageToListPackageDTO(List<Package> packages, List<PackageDTO> result){
    for(Package packagee : packages){
      PackageDTO newPackageDTO = convertPackageToPackageDTO(packagee);
      result.add(newPackageDTO);
    }
  }


  private PackageDTO savePackage(PackageRequestDTO packageRequestDTO) {
    try{

      ServiceUtils.errors.clear();
      List<Long> requestPackageInDayIds = packageRequestDTO.getPackageDTO().getPackageInDayIds();

      List<Long> requestDestinationIds = new ArrayList<>();
      for (ServiceDestinationDTO serviceDestinationDTO : packageRequestDTO.getServiceDestinationDTOS()){
        Long destinationId = destinationRepository.findDestinationByServiceId(serviceDestinationDTO.getId()).getId();
        requestDestinationIds.add(destinationId);
      }

      Package packagee;

      if (requestPackageInDayIds != null){
        ServiceUtils.validatePackageInDayIds(requestPackageInDayIds, packageInDayRepository);
      }
      if (requestDestinationIds != null){
        ServiceUtils.validateDestinationIds(requestDestinationIds, destinationRepository);
      }
      if (!ServiceUtils.errors.isEmpty()){
        throw new CustomValidationException(ServiceUtils.errors);
      }

      if (packageRequestDTO.getPackageDTO().getId() != null){
        Package oldEntity = packageRepository.findById(packageRequestDTO.getPackageDTO().getId());
        Package tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
        packagee = (Package) genericConverter.toEntity(packageRequestDTO.getPackageDTO(), Package.class);
        packagee = ServiceUtils.fillMissingAttribute(packagee, tempOldEntity);
        //packageInDestinationRepository.deleteAllByPackageeId(packageRequestDTO.getPackageDTO().getId());
        loadPackageInDestinationFromListDestinationIds(requestDestinationIds, packagee.getId());

        packageRepository.save(packagee);

      } else {

        packageRequestDTO.getPackageDTO().setStatus(true);
        packagee = (Package) genericConverter.toEntity(packageRequestDTO.getPackageDTO(), Package.class);
        packageRepository.save(packagee);
        loadPackageInDestinationFromListDestinationIds(requestDestinationIds, packagee.getId());

      }
      PackageDTO result = convertPackageToPackageDTO(packagee);
      if (packageRequestDTO.getPackageDTO().getId() == null){
        result.setDestinationIds(requestDestinationIds);
      }
      return result;


    } catch (Exception ex){
      ex.printStackTrace();
      return null;
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
          PackageInDestination packageInDestination = packageInDestinationRepository.findByPackageeIdAndDestinationId(packageId, destinationId);
          if(packageInDestination == null){
            packageInDestination = new PackageInDestination();
            packageInDestination.setPackagee(packagee);
            packageInDestination.setDestination(destination);
          } else{
            packageInDestination.setPackagee(packagee);
            packageInDestination.setDestination(destination);
          }

          packageInDestinationRepository.save(packageInDestination);
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

  @Override
  public ResponseEntity<?> save(PackageRequestDTO packageRequestDTO) {
    try {

      PackageDTO packageDTO = savePackage(packageRequestDTO);
      List<Long> serviceIds = new ArrayList<>();
      if(packageDTO != null){
        List<ServiceDestinationDTO> serviceDestinationDTOS = packageRequestDTO.getServiceDestinationDTOS();

        if(!serviceDestinationDTOS.isEmpty()){
          for (ServiceDestinationDTO serviceDestinationDTO : serviceDestinationDTOS){
            Long destinationId = destinationRepository.findDestinationByServiceId(serviceDestinationDTO.getId()).getId();
            PackageInDestination packageInDestination = packageInDestinationRepository.findByPackageeIdAndDestinationId(packageDTO.getId(),destinationId);
            packageInDestination.setStartTime(serviceDestinationDTO.getStartTime());
            packageInDestination.setEndTime(serviceDestinationDTO.getEndTime());
            packageInDestination.setTransportation(serviceDestinationDTO.getTransportation());
            packageInDestinationRepository.save(packageInDestination);
            Servicee servicee = serviceRepository.findById(serviceDestinationDTO.getId());
            serviceIds.add(servicee.getId());
            ServiceInPackage sip = serviceInPackageRepository.findByServiceeIdAndPackageInDestinationId(servicee.getId(), packageInDestination.getId());
            if(sip == null){
              sip = new ServiceInPackage();
              sip.setServicee(servicee);
              sip.setPackageInDestination(packageInDestination);
            }else{
              sip.setServicee(servicee);
              sip.setPackageInDestination(packageInDestination);
            }

            serviceInPackageRepository.save(sip);
          }

        }
      }
      PackageServiceResponse result = new PackageServiceResponse();
      result.setPackageDTO(packageDTO);
      result.setServiceId(serviceIds);
      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    } catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }

  }

  @Override
  public ResponseEntity<?> update(PackageRequestDTO packageRequestDTO) {
    try {
      packageInDestinationRepository.deleteAllByPackageeId(packageRequestDTO.getPackageDTO().getId());
      List<PackageInDestination> packageInDestinations = packageInDestinationRepository.findAllByPackageeId(packageRequestDTO.getPackageDTO().getId());
      for(PackageInDestination packageInDestination : packageInDestinations){
        serviceInPackageRepository.deleteAllByPackageInDestinationId(packageInDestination.getId());
      }
      PackageDTO packageDTO = savePackage(packageRequestDTO);
      List<Long> serviceIds = new ArrayList<>();
      if(packageDTO != null){
        List<ServiceDestinationDTO> serviceDestinationDTOS = packageRequestDTO.getServiceDestinationDTOS();

        if(!serviceDestinationDTOS.isEmpty()){
          for (ServiceDestinationDTO serviceDestinationDTO : serviceDestinationDTOS){
            Long destinationId = destinationRepository.findDestinationByServiceId(serviceDestinationDTO.getId()).getId();
            PackageInDestination packageInDestination = packageInDestinationRepository.findByPackageeIdAndDestinationId(packageDTO.getId(),destinationId);
            packageInDestination.setStartTime(serviceDestinationDTO.getStartTime());
            packageInDestination.setEndTime(serviceDestinationDTO.getEndTime());
            packageInDestination.setTransportation(serviceDestinationDTO.getTransportation());
            packageInDestinationRepository.save(packageInDestination);
            Servicee servicee = serviceRepository.findById(serviceDestinationDTO.getId());
            serviceIds.add(servicee.getId());
            ServiceInPackage sip = serviceInPackageRepository.findByServiceeIdAndPackageInDestinationId(servicee.getId(), packageInDestination.getId());
            if(sip == null){
              sip = new ServiceInPackage();
              sip.setServicee(servicee);
              sip.setPackageInDestination(packageInDestination);
            }else{
              sip.setServicee(servicee);
              sip.setPackageInDestination(packageInDestination);
            }

            serviceInPackageRepository.save(sip);
          }

        }
      }
      PackageServiceResponse result = new PackageServiceResponse();
      result.setPackageDTO(packageDTO);
      result.setServiceId(serviceIds);
      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    } catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }

  }

}
