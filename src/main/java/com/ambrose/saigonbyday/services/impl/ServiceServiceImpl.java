package com.ambrose.saigonbyday.services.impl;


import com.ambrose.saigonbyday.config.CustomValidationException;
import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.ServiceDTO;
import com.ambrose.saigonbyday.entities.PackageInDestination;
import com.ambrose.saigonbyday.entities.ServiceInPackage;
import com.ambrose.saigonbyday.entities.Servicee;
import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.repository.DestinationRepository;
import com.ambrose.saigonbyday.repository.PackageInDestinationRepository;
import com.ambrose.saigonbyday.repository.ServiceInPackageRepository;
import com.ambrose.saigonbyday.repository.ServiceRepository;
import com.ambrose.saigonbyday.repository.UserRepository;
import com.ambrose.saigonbyday.services.ServiceService;
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
public class ServiceServiceImpl implements ServiceService {

  private final PackageInDestinationRepository packageInDestinationRepository;
  private final DestinationRepository destinationRepository;
  private final ServiceInPackageRepository serviceInPackageRepository;
  private final ServiceRepository serviceRepository;
  private final GenericConverter genericConverter;
  private final UserRepository userRepository;

  @Override
  public ResponseEntity<?> findById(Long id) {
    try{
      Servicee service = serviceRepository.findByStatusIsTrueAndId(id);
      if (service != null){
        ServiceDTO result = convertServiceToServiceDTO(service);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
      } else {
        return ResponseUtil.error("Service not found", "Cannot Find Service", HttpStatus.NOT_FOUND);
      }
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }

  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    try {
      Pageable pageable = PageRequest.of(page - 1, limit);
      List<Servicee> services = serviceRepository.findAllByStatusIsTrue(pageable);
      List<ServiceDTO> result = new ArrayList<>();

      convertListServiceToListServiceDTO(services, result);

      return ResponseUtil.getCollection(result,
          HttpStatus.OK,
          "Fetched successfully",
          page,
          limit,
          serviceRepository.countAllByStatusIsTrue());

    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }
  }

  private void convertListServiceToListServiceDTO(List<Servicee> services, List<ServiceDTO> result){
    for(Servicee service : services){
      ServiceDTO newServiceDTO = convertServiceToServiceDTO(service);
      result.add(newServiceDTO);
    }
  }

  @Override
  public ResponseEntity<?> save(ServiceDTO serviceDTO) {
    try{
      ServiceUtils.errors.clear();
      List<Long> requestPackageInDestinationIds = serviceDTO.getPackageInDestinationIds();
      Long requestUserId = serviceDTO.getUserId();
      Long requestDestinationId = serviceDTO.getDestinationId();
      Servicee service;

      if (requestPackageInDestinationIds != null){
        ServiceUtils.validatePackageInDestinationIds(requestPackageInDestinationIds, packageInDestinationRepository);
      }
      if (requestDestinationId != null){
        ServiceUtils.validateDestinationIds(List.of(requestDestinationId),destinationRepository);
      }
      if (requestUserId != null){
        ServiceUtils.validateUserIds(List.of(requestUserId), userRepository);
      }
      if (!ServiceUtils.errors.isEmpty()) {
        throw new CustomValidationException(ServiceUtils.errors);
      }

      if (serviceDTO.getId() != null){
        Servicee oldEntity = serviceRepository.findById(serviceDTO.getId());
        Servicee tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
        service = (Servicee) genericConverter.toEntity(serviceDTO, Servicee.class);
        service = ServiceUtils.fillMissingAttribute(service, tempOldEntity);
        serviceInPackageRepository.deleteAllByServiceeId(serviceDTO.getId());
        loadServiceInPackageFromListPackageInDestinationId(requestPackageInDestinationIds, service.getId());

        if (requestUserId != null){
          service.setUser(userRepository.findUserById(requestUserId));
        }
        if (requestDestinationId != null){
          service.setDestination(destinationRepository.findById(requestDestinationId));
        }
        serviceRepository.save(service);
      } else{
        serviceDTO.setStatus(true);
        service = (Servicee) genericConverter.toEntity(serviceDTO, Servicee.class);
        if (requestUserId != null){
          service.setUser(userRepository.findUserById(requestUserId));
        }
        if (requestDestinationId != null){
          service.setDestination(destinationRepository.findById(requestDestinationId));
        }
        serviceRepository.save(service);
        loadServiceInPackageFromListPackageInDestinationId(requestPackageInDestinationIds, service.getId());
      }
      ServiceDTO result = convertServiceToServiceDTO(service);
      if (serviceDTO.getId() == null){
        result.setPackageInDestinationIds(requestPackageInDestinationIds);
      }
      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");

    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }

  }

  private ServiceDTO convertServiceToServiceDTO(Servicee service){
    ServiceDTO newServiceDTO = (ServiceDTO) genericConverter.toDTO(service, ServiceDTO.class);
    List<PackageInDestination> packageInDestinations = serviceInPackageRepository.findPackageInDestinationsByServiceId(service.getId());

    if (packageInDestinations == null){
      newServiceDTO.setPackageInDestinationIds(null);
    } else {
      List<Long> packageInDestinationIds = packageInDestinations.stream()
          .map(PackageInDestination::getId)
          .toList();
      newServiceDTO.setPackageInDestinationIds(packageInDestinationIds);
    }

    if (service.getUser() == null){
      newServiceDTO.setUserId(null);
    }else{
      User user = serviceRepository.findUserByUserId(service.getUser().getId());
      newServiceDTO.setUserId(user.getId());
    }
    return newServiceDTO;
  }

  private void loadServiceInPackageFromListPackageInDestinationId(List<Long> requestPackageInDestinationIds, Long serviceId){
    if (requestPackageInDestinationIds != null && !requestPackageInDestinationIds.isEmpty()){
      for (Long packageInDestinationId : requestPackageInDestinationIds){
        PackageInDestination packageInDestination = packageInDestinationRepository.findById(packageInDestinationId);
        Servicee service = serviceRepository.findById(serviceId);
        if(packageInDestination != null && service != null){
          ServiceInPackage sip = new ServiceInPackage();
          sip.setServicee(service);
          sip.setPackageInDestination(packageInDestination);
          serviceInPackageRepository.save(sip);
        }
      }
    }
  }

  @Override
  public ResponseEntity<?> changeStatus(Long id) {
    try{
      Servicee service = serviceRepository.findById(id);
      if (service != null){
        if (service.getStatus()){
          service.setStatus(false);
        }else{
          service.setStatus(true);
        }
        serviceRepository.save(service);
        return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
      }else{
        return ResponseUtil.error("Service not found", "Cannot change status of non-existing Service", HttpStatus.NOT_FOUND);
      }
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }
  }
  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Servicee> entities = serviceRepository.findAllBy(pageable);
    List<ServiceDTO> result = new ArrayList<>();

    convertListServiceToListServiceDTO(entities, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        serviceRepository.countAllByStatusIsTrue());
  }

  @Override
  public Boolean checkExist(Long id) {
    Servicee service = serviceRepository.findById(id);
    return service != null;
  }
}
