package com.ambrose.saigonbyday.repository.specification;

import com.ambrose.saigonbyday.entities.City;
import com.ambrose.saigonbyday.entities.Destination;
import com.ambrose.saigonbyday.entities.Package;
import com.ambrose.saigonbyday.entities.PackageInDay;
import com.ambrose.saigonbyday.entities.PackageInDestination;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.Date;
import org.springframework.data.jpa.domain.Specification;

public class PackageInDaySpecification {

//  public static Specification<PackageInDay> hasPackageName(String packageName) {
//    return (root, query, cb) -> packageName == null ? cb.conjunction() : cb.like(root.get("packagee").get("name"), "%" + packageName + "%");
//  }
public static Specification<PackageInDay> hasPackageName(String packageName) {
  return (root, query, cb) -> {
    if (packageName == null || packageName.isEmpty()) {
      return cb.conjunction();
    }
    return cb.like(cb.lower(root.join("packagee").get("name")), "%" + packageName.toLowerCase() + "%");
  };
}

  public static Specification<PackageInDay> hasPriceBetween(Float minPrice, Float maxPrice) {
    return (root, query, cb) -> {
      if (minPrice != null && maxPrice != null) {
        return cb.between(root.get("price"), minPrice, maxPrice);
      } else if (minPrice != null) {
        return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
      } else if (maxPrice != null) {
        return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
      } else {
        return cb.conjunction();
      }
    };
  }

  public static Specification<PackageInDay> hasDate(Date date) {
    return (root, query, cb) -> date == null ? cb.conjunction() : cb.equal(root.get("date"), date);
  }

  public static Specification<PackageInDay> hasNumberAttendanceBetween(Integer minAttendance, Integer maxAttendance) {
    return (root, query, cb) -> {
      if (minAttendance != null && maxAttendance != null) {
        return cb.between(root.get("numberAttendance"), minAttendance, maxAttendance);
      } else if (minAttendance != null) {
        return cb.greaterThanOrEqualTo(root.get("numberAttendance"), minAttendance);
      } else if (maxAttendance != null) {
        return cb.lessThanOrEqualTo(root.get("numberAttendance"), maxAttendance);
      } else {
        return cb.conjunction();
      }
    };
  }

  public static Specification<PackageInDay> hasCity(String cityName) {
    return (root, query, cb) -> cityName == null ? cb.conjunction() : cb.equal(root.join("packagee").join("packageInDestinations").join("destination").join("city").get("name"), cityName);
  }
//public static Specification<PackageInDay> hasCity(String cityName) {
//  return (root, query, cb) ->
//      cb.equal(root.join("packagee").join("packageInDestinations").join("destination").get("city").get("name"), cityName);
//}

}
