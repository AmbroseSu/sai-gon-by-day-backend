package com.ambrose.saigonbyday.services.impl;



import com.ambrose.saigonbyday.config.CustomValidationException;
import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.PackageInDayDTO;
import com.ambrose.saigonbyday.dto.PackageInDaySaleDTO;
import com.ambrose.saigonbyday.dto.request.PackageInDaySearchRequest;
import com.ambrose.saigonbyday.entities.Destination;
import com.ambrose.saigonbyday.entities.Gallery;
import com.ambrose.saigonbyday.entities.Order;
import com.ambrose.saigonbyday.entities.OrderDetails;
import com.ambrose.saigonbyday.entities.Package;
import com.ambrose.saigonbyday.entities.PackageInDay;
import com.ambrose.saigonbyday.repository.OrderDetailsRepository;
import com.ambrose.saigonbyday.repository.OrderRepository;
import com.ambrose.saigonbyday.repository.PackageInDayRepository;
import com.ambrose.saigonbyday.repository.PackageInDestinationRepository;
import com.ambrose.saigonbyday.repository.PackageRepository;
import com.ambrose.saigonbyday.repository.specification.PackageInDaySpecification;
import com.ambrose.saigonbyday.services.PackageInDayService;
import com.ambrose.saigonbyday.services.ServiceUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PackageInDayServiceImpl implements PackageInDayService {

  private final GenericConverter genericConverter;
  private final PackageRepository packageRepository;
  private final OrderRepository orderRepository;
  private final PackageInDayRepository packageInDayRepository;
  private final OrderDetailsRepository orderDetailsRepository;
  private final PackageInDestinationRepository packageInDestinationRepository;

  @Override
  public ResponseEntity<?> findById(Long id) {
    try{
      PackageInDay packageInDay = packageInDayRepository.findByStatusIsTrueAndId(id);
      if (packageInDay != null){
        PackageInDayDTO result = convertPackageInDayToPackageInDayDTO(packageInDay);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");

      } else {
        return ResponseUtil.error("Package In Day not found", "Cannot find Package In Day", HttpStatus.NOT_FOUND);
      }

    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    try{
      Pageable pageable = PageRequest.of(page - 1, limit);
      List<PackageInDay> packageInDays = packageInDayRepository.findAllByStatusIsTrue(pageable);
      List<PackageInDayDTO> result = new ArrayList<>();

      convertListPackageInDayToListPackageInDayDTO(packageInDays, result);

      return ResponseUtil.getCollection(result,
          HttpStatus.OK,
          "Fetched successfully",
          page,
          limit,
          packageInDayRepository.countAllByStatusIsTrue());
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }
  }

  private void convertListPackageInDayToListPackageInDayDTO(List<PackageInDay> packageInDays, List<PackageInDayDTO> result){
    for (PackageInDay packageInDay : packageInDays){
      PackageInDayDTO newPackageInDayDTO = convertPackageInDayToPackageInDayDTO(packageInDay);
      result.add(newPackageInDayDTO);
    }
  }

  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<PackageInDay> entities = packageInDayRepository.findAllBy(pageable);
    List<PackageInDayDTO> result = new ArrayList<>();

    convertListPackageInDayToListPackageInDayDTO(entities, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        packageInDayRepository.countAllByStatusIsTrue());
  }

  @Override
  public ResponseEntity<?> save(PackageInDayDTO packageInDayDTO) {
    try{
      ServiceUtils.errors.clear();
      List<Long> requestOrderIds = packageInDayDTO.getOrderIds();
      Long requestPackageId = packageInDayDTO.getPackageId();

      PackageInDay packageInDay;

      if (requestPackageId != null){
        ServiceUtils.validatePackageIds(List.of(requestPackageId), packageRepository);
      }
      if (requestOrderIds != null){
        ServiceUtils.validateOrderIds(requestOrderIds, orderRepository);
      }

      if (!ServiceUtils.errors.isEmpty()){
        throw new CustomValidationException(ServiceUtils.errors);
      }

      if (packageInDayDTO.getId() != null){
        PackageInDay oldEntity = packageInDayRepository.findById(packageInDayDTO.getId());
        PackageInDay tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
        packageInDay = (PackageInDay) genericConverter.toEntity(packageInDayDTO, PackageInDay.class);
        packageInDay = ServiceUtils.fillMissingAttribute(packageInDay, tempOldEntity);
        orderDetailsRepository.deleteAllByPackageInDayId(packageInDay.getId());
        loadOrderDetailFromListOrderIds(requestOrderIds, packageInDay.getId());

        if (requestPackageId != null){
          packageInDay.setPackagee(packageRepository.findById(requestPackageId));
        }
        packageInDayRepository.save(packageInDay);

      } else {
        packageInDayDTO.setStatus(true);
        packageInDay = (PackageInDay) genericConverter.toEntity(packageInDayDTO, PackageInDay.class);
        if (requestPackageId != null){
          packageInDay.setPackagee(packageRepository.findById(requestPackageId));
        }
        packageInDayRepository.save(packageInDay);
        loadOrderDetailFromListOrderIds(requestOrderIds, packageInDay.getId());

      }

      PackageInDayDTO result = convertPackageInDayToPackageInDayDTO(packageInDay);
      if (packageInDayDTO.getId() == null){
        result.setOrderIds(requestOrderIds);
      }
      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");

    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(),"Failed", HttpStatus.BAD_REQUEST);
    }
  }

  private PackageInDayDTO convertPackageInDayToPackageInDayDTO(PackageInDay packageInDay){
    PackageInDayDTO newPackageInDayDTO = (PackageInDayDTO) genericConverter.toDTO(packageInDay, PackageInDayDTO.class);
    List<Order> orders = orderDetailsRepository.findOrdersByPackageInDayId(packageInDay.getId());
    if (orders == null){
      newPackageInDayDTO.setOrderIds(null);
    } else {
      List<Long> orderIds = orders.stream()
          .map(Order::getId)
          .toList();
      newPackageInDayDTO.setOrderIds(orderIds);
    }
    if (packageInDay.getPackagee() == null){
      newPackageInDayDTO.setPackageId(null);
    } else {
      Package packagee = packageInDayRepository.findPackageByPackageId(packageInDay.getPackagee().getId());
      newPackageInDayDTO.setPackageId(packagee.getId());
    }
    return newPackageInDayDTO;

  }

  private void loadOrderDetailFromListOrderIds(List<Long> requestOrderIds, Long packageInDayId){
    if (requestOrderIds != null && !requestOrderIds.isEmpty()){
      for (Long orderId : requestOrderIds){
        Order order = orderRepository.findById(orderId);
        PackageInDay packageInDay = packageInDayRepository.findById(packageInDayId);
        if (order != null && packageInDay != null){
          OrderDetails ord = new OrderDetails();
          ord.setPackageInDay(packageInDay);
          ord.setOrder(order);
          orderDetailsRepository.save(ord);
        }
      }
    }
  }

  @Override
  public ResponseEntity<?> changeStatus(Long id) {
    try{
      PackageInDay packageInDay = packageInDayRepository.findById(id);
      if (packageInDay != null){
        if (packageInDay.getStatus()){
          packageInDay.setStatus(false);
        } else {
          packageInDay.setStatus(true);
        }
        packageInDayRepository.save(packageInDay);
        return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
      } else {
        return ResponseUtil.error("Package In Day not found", "Cannot change status of non-existing Package In Day", HttpStatus.NOT_FOUND);
      }
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public Boolean checkExist(Long id) {
    PackageInDay packageInDay = packageInDayRepository.findById(id);
    return packageInDay != null;
  }

  @Override
  public ResponseEntity<?> findAllSale(int page, int limit) {
    try{
      Pageable pageable = PageRequest.of(page - 1, limit);
      List<PackageInDay> packageInDays = packageInDayRepository.findAllByStatusIsTrue(pageable);
      List<PackageInDaySaleDTO> packageInDaySaleDTOS = new ArrayList<>();
      for (PackageInDay packageInDay : packageInDays){
        packageInDaySaleDTOS.add(convertToPackageInDaySaleDTO(packageInDay));
      }
      return ResponseUtil.getCollection(packageInDaySaleDTOS,
          HttpStatus.OK,
          "Fetched successfully",
          page,
          limit,
          packageInDayRepository.countAllByStatusIsTrue());
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(),"Failed", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public ResponseEntity<?> findAllWithSearchSortFilter(PackageInDaySearchRequest packageInDaySearchRequest) {
    try {
      Sort.Direction direction = "asc".equalsIgnoreCase(packageInDaySearchRequest.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
      Specification<PackageInDay> packageInDaySpecification = Specification
          .where(PackageInDaySpecification.hasPackageName(packageInDaySearchRequest.getPackageName()))
          .and(PackageInDaySpecification.hasPriceBetween(packageInDaySearchRequest.getMinPrice(),
              packageInDaySearchRequest.getMaxPrice()))
          .and(PackageInDaySpecification.hasDate(packageInDaySearchRequest.getDate()))
          .and(PackageInDaySpecification.hasNumberAttendanceBetween(
              packageInDaySearchRequest.getMinAttendance(),
              packageInDaySearchRequest.getMaxAttendance()))
          .and(PackageInDaySpecification.hasCity(packageInDaySearchRequest.getCityName()));

      Pageable pageable = PageRequest.of(
          packageInDaySearchRequest.getPage(),
          packageInDaySearchRequest.getLimit(),
          Sort.by(direction, "price"));
      Page<PackageInDay> packageInDays = packageInDayRepository.findAll(packageInDaySpecification, pageable);
      List<PackageInDay> result = packageInDays.getContent();

      return ResponseUtil.getCollection(result,
          HttpStatus.OK,
          "Fetched successfully",
          packageInDaySearchRequest.getPage(),
          packageInDaySearchRequest.getLimit(),
          packageInDayRepository.countAllByStatusIsTrue());



    } catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(),"Failed", HttpStatus.BAD_REQUEST);
    }
  }

  private PackageInDaySaleDTO convertToPackageInDaySaleDTO(PackageInDay packageInDay){
    PackageInDaySaleDTO packageInDaySaleDTO = new PackageInDaySaleDTO();
    packageInDaySaleDTO.setId(packageInDay.getId());
    packageInDaySaleDTO.setDate(packageInDay.getDate());
    packageInDaySaleDTO.setCode(packageInDay.getCode());
    packageInDaySaleDTO.setPrice(packageInDay.getPrice());
    packageInDaySaleDTO.setNumberAttendance(packageInDay.getNumberAttendance());

    Package packagee = packageInDay.getPackagee();
    if(packagee != null){
      packageInDaySaleDTO.setPackageName(packagee.getName());
      packageInDaySaleDTO.setPackageDescription(packagee.getDescription());
      packageInDaySaleDTO.setPackageShortDescription(
          packagee.getShortDescription());
      packageInDaySaleDTO.setPackageStartTime(packagee.getStartTime());
      packageInDaySaleDTO.setPackageEndTime(packagee.getEndTime());
      List<Destination> destinations = packageInDestinationRepository.findDestinationsByPackageId(packageInDay.getPackagee().getId());

      List<String> galleries = new ArrayList<>();

      for(Destination destination : destinations){
        Gallery gallery = destination.getGallery();
        if (gallery != null){
          if(gallery.getImageNo1() != null){
            galleries.add(gallery.getImageNo1());
          }
          if(gallery.getImageNo2() != null){
            galleries.add(gallery.getImageNo2());
          }
          if(gallery.getImageNo3() != null){
            galleries.add(gallery.getImageNo3());
          }
        }
      }

      packageInDaySaleDTO.setGalleryUrls(galleries);
    }
    return packageInDaySaleDTO;
  }




}
