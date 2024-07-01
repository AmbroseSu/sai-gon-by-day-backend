package com.ambrose.saigonbyday.services.impl;


import com.ambrose.saigonbyday.config.CustomValidationException;
import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.DestinationDTO;
import com.ambrose.saigonbyday.entities.City;
import com.ambrose.saigonbyday.entities.Destination;
import com.ambrose.saigonbyday.entities.Package;
import com.ambrose.saigonbyday.entities.PackageInDestination;
import com.ambrose.saigonbyday.repository.CityRepository;
import com.ambrose.saigonbyday.repository.DestinationRepository;
import com.ambrose.saigonbyday.repository.GalleryRepository;
import com.ambrose.saigonbyday.repository.PackageInDestinationRepository;
import com.ambrose.saigonbyday.repository.PackageRepository;
import com.ambrose.saigonbyday.repository.ServiceRepository;
import com.ambrose.saigonbyday.services.DestinationService;
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
public class DestinationServiceImpl implements DestinationService {

  private final DestinationRepository destinationRepository;
  private final GenericConverter genericConverter;
  private final PackageInDestinationRepository packageInDestinationRepository;
  private final PackageRepository packageRepository;
  private final CityRepository cityRepository;
  private final ServiceRepository serviceRepository;
  private final GalleryRepository galleryRepository;


  @Override
  public ResponseEntity<?> findById(Long id) {
    try{
      Destination destination = destinationRepository.findByStatusIsTrueAndId(id);
      if (destination != null){
        DestinationDTO result = convertDestinationToDestinationDTO(destination);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
      } else {
        return ResponseUtil.error("Destination not found", "Cannot find Destination", HttpStatus.NOT_FOUND);
      }
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }

  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    try{
      Pageable pageable = PageRequest.of(page - 1, limit);
      List<Destination> destinations = destinationRepository.findAllByStatusIsTrue(pageable);
      List<DestinationDTO> result = new ArrayList<>();

      convertListDestinationToListDestinationDTO(destinations, result);

      return ResponseUtil.getCollection(result,
          HttpStatus.OK,
          "Fetched successfully",
          page,
          limit,
          destinationRepository.countAllByStatusIsTrue());
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }


  }

  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Destination> entities = destinationRepository.findAllBy(pageable);
    List<DestinationDTO> result = new ArrayList<>();

    convertListDestinationToListDestinationDTO(entities, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        destinationRepository.countAllByStatusIsTrue());
  }

  private void convertListDestinationToListDestinationDTO(List<Destination> destinations, List<DestinationDTO> result){
    for (Destination destination : destinations){
      DestinationDTO newDestinationDTO = convertDestinationToDestinationDTO(destination);
      result.add(newDestinationDTO);
    }
  }

  @Override
  public ResponseEntity<?> save(DestinationDTO destinationDTO) {
    try{
      ServiceUtils.errors.clear();
      List<Long> requestServiceIds = destinationDTO.getServiceIds();
      List<Long> requestPackageIds = destinationDTO.getPackageIds();
      Long requestCityId = destinationDTO.getCityId();
      Long requestGalleryId = destinationDTO.getGalleryId();
      Destination destination;

      if (requestServiceIds != null){
      ServiceUtils.validateServiceIds(requestServiceIds, serviceRepository);
      }
      if (requestPackageIds != null){
        ServiceUtils.validatePackageIds(requestPackageIds, packageRepository);
      }
      if (requestCityId != null){
        ServiceUtils.validateCityIds(List.of(requestCityId),cityRepository);
      }
      if (requestGalleryId != null){
        ServiceUtils.validateGalleryIds(List.of(requestGalleryId), galleryRepository);
      }

      if (!ServiceUtils.errors.isEmpty()){
        throw new CustomValidationException(ServiceUtils.errors);
      }

      if (destinationDTO.getId() != null){
        Destination oldEntity = destinationRepository.findById(destinationDTO.getId());
        Destination tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
        destination = (Destination) genericConverter.toEntity(destinationDTO, Destination.class);
        destination = ServiceUtils.fillMissingAttribute(destination, tempOldEntity);
        packageInDestinationRepository.deleteAllByDestinationId(destinationDTO.getId());
        loadPackageInDestinationFromListPackageIds(requestPackageIds, destination.getId());

        if (requestCityId != null){
          destination.setCity(cityRepository.findById(requestCityId));
        }
        if (requestGalleryId != null){
          destination.setGallery(galleryRepository.findById(requestGalleryId));
        }
        destinationRepository.save(destination);

      } else {
        destinationDTO.setStatus(true);
        destination = (Destination) genericConverter.toEntity(destinationDTO, Destination.class);
        if (requestCityId != null){
          destination.setCity(cityRepository.findById(requestCityId));
        }
        if (requestGalleryId != null){
          destination.setGallery(galleryRepository.findById(requestGalleryId));
        }
        destinationRepository.save(destination);
        loadPackageInDestinationFromListPackageIds(requestPackageIds, destination.getId());
      }
      DestinationDTO result = convertDestinationToDestinationDTO(destination);
      if (destinationDTO.getId() == null){
        result.setPackageIds(requestPackageIds);
      }
      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(),"Failed", HttpStatus.BAD_REQUEST);
    }
  }

  private DestinationDTO convertDestinationToDestinationDTO(Destination destination){
    DestinationDTO newDestinationDTO = (DestinationDTO) genericConverter.toDTO(destination, DestinationDTO.class);
    List<Package> packages = packageInDestinationRepository.findPackagesByDestinationId(destination.getId());
    if (packages == null){
      newDestinationDTO.setPackageIds(null);
    } else {
      List<Long> packageIds = packages.stream()
          .map(Package::getId)
          .toList();
      newDestinationDTO.setPackageIds(packageIds);
    }
    if (destination.getCity() == null){
      newDestinationDTO.setCityId(null);
    } else{
      City city = destinationRepository.findCityByCityId(destination.getCity().getId());
      newDestinationDTO.setCityId(city.getId());
    }
    return newDestinationDTO;

  }

  private void loadPackageInDestinationFromListPackageIds(List<Long> requestPackageIds, Long destinationId){
    if (requestPackageIds != null && !requestPackageIds.isEmpty()){
      for (Long packageId : requestPackageIds){
        Package packagee = packageRepository.findById(packageId);
        Destination destination = destinationRepository.findById(destinationId);
        if (packagee != null && destination != null){
          PackageInDestination pid = new PackageInDestination();
          pid.setDestination(destination);
          pid.setPackagee(packagee);
          packageInDestinationRepository.save(pid);
        }
      }
    }
  }

  @Override
  public ResponseEntity<?> changeStatus(Long id) {
    try{
      Destination destination = destinationRepository.findById(id);
      if (destination != null){
        if(destination.getStatus()){
          destination.setStatus(false);
        } else {
          destination.setStatus(true);
        }
        destinationRepository.save(destination);
        return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
      } else {
        return ResponseUtil.error("Destination not found", "Cannot change status of non-existing Destination", HttpStatus.NOT_FOUND);
      }
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }

  }

  @Override
  public Boolean checkExist(Long id) {
    Destination destination = destinationRepository.findById(id);
    return destination != null;
  }
}
