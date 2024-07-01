package com.ambrose.saigonbyday.services;


import com.ambrose.saigonbyday.repository.CityRepository;
import com.ambrose.saigonbyday.repository.DestinationRepository;
import com.ambrose.saigonbyday.repository.GalleryRepository;
import com.ambrose.saigonbyday.repository.OrderRepository;
import com.ambrose.saigonbyday.repository.PackageInDayRepository;
import com.ambrose.saigonbyday.repository.PackageInDestinationRepository;
import com.ambrose.saigonbyday.repository.PackageRepository;
import com.ambrose.saigonbyday.repository.ServiceRepository;
import com.ambrose.saigonbyday.repository.UserRepository;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.BeanUtils;

public class ServiceUtils {
  public static List<String> errors = new ArrayList<>();

  public static <T> T fillMissingAttribute(T entity, T tempOldEntity) {
    List<Field> allFields = new ArrayList<>();
    Class<?> currentClass = entity.getClass();
    try {
      // Traverse class hierarchy to collect fields from all superclasses
      while (currentClass != null) {
        Field[] declaredFields = currentClass.getDeclaredFields();
        allFields.addAll(Arrays.asList(declaredFields));
        currentClass = currentClass.getSuperclass();
      }

      // Iterate over all fields
      for (Field field : allFields) {
        field.setAccessible(true); // Enable access to private fields if any

        try {
          Object newValue = field.get(entity); // Get the value of the field for the newEntity
          if (newValue == null) {
            // If the value is null, get the corresponding value from oldEntity
            Object oldValue = field.get(tempOldEntity);
            field.set(entity, oldValue); // Set the value of the field for the newEntity
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
      return entity;
    } catch (Exception e) {
      throw e;
    }
  }

  public static <T> T cloneFromEntity(T t) {
    T clone;
    try {
      clone = (T) t.getClass().newInstance();
      BeanUtils.copyProperties(t, clone);
    } catch (IllegalAccessException | InstantiationException e) {
      e.printStackTrace(); // Handle the exception appropriately
      return null;
    }
    return clone;
  }

  public static void validateDestinationIds(List<Long> destinationIds, DestinationRepository destinationRepository) {
    for (Long destinationId : destinationIds) {
      if (destinationRepository.findById(destinationId) == null) {
        errors.add("Destination with id " + destinationId + " does not exist");
      }
    }
  }

  public static void validateServiceIds(List<Long> serviceIds,  ServiceRepository serviceRepository) {
    for (Long serviceId : serviceIds) {
      if (serviceRepository.findById(serviceId) == null) {
        errors.add("Destination with id " + serviceId + " does not exist");
      }
    }
  }
  public static void validatePackageIds(List<Long> packageIds,  PackageRepository packageRepository) {
    for (Long packageId : packageIds) {
      if (packageRepository.findById(packageId) == null) {
        errors.add("Destination with id " + packageId + " does not exist");
      }
    }
  }

  public static void validatePackageInDestinationIds(List<Long> packageInDestinationIds,  PackageInDestinationRepository packageInDestinationRepository) {
    for (Long packageInDestinationId : packageInDestinationIds) {
      if (packageInDestinationRepository.findById(packageInDestinationId) == null) {
        errors.add("Destination with id " + packageInDestinationId + " does not exist");
      }
    }
  }
  public static void validateCityIds(List<Long> cityIds,  CityRepository cityRepository) {
    for (Long cityId : cityIds) {
      if (cityRepository.findById(cityId) == null) {
        errors.add("Destination with id " + cityId + " does not exist");
      }
    }
  }

  public static void validateGalleryIds(List<Long> galleryIds,  GalleryRepository galleryRepository) {
    for (Long galleryId : galleryIds) {
      if (galleryRepository.findById(galleryId) == null) {
        errors.add("Destination with id " + galleryId + " does not exist");
      }
    }
  }

  public static void validateUserIds(List<Long> userIds,  UserRepository userRepository) {
    for (Long userId : userIds) {
      if (userRepository.findUserById(userId) == null) {
        errors.add("Destination with id " + userId + " does not exist");
      }
    }
  }
  public static void validatePackageInDayIds(List<Long> packageInDayIds,  PackageInDayRepository packageInDayRepository) {
    for (Long packageInDayId : packageInDayIds) {
      if (packageInDayRepository.findById(packageInDayId) == null) {
        errors.add("Package In Day with id " + packageInDayId + " does not exist");
      }
    }
  }

  public static void validateOrderIds(List<Long> orderIds,  OrderRepository orderRepository) {
    for (Long orderId : orderIds) {
      if (orderRepository.findById(orderId) == null) {
        errors.add("Order with id " + orderId + " does not exist");
      }
    }
  }


}
