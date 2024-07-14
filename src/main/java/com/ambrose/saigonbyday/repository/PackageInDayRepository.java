package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.Package;
import com.ambrose.saigonbyday.entities.PackageInDay;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageInDayRepository extends JpaRepository<PackageInDay, String>,
    JpaSpecificationExecutor<PackageInDay> {

  PackageInDay findById(Long id);
  PackageInDay findByStatusIsTrueAndId(Long id);

  List<PackageInDay> findAllBy(Pageable pageable);
  List<PackageInDay> findAllByStatusIsTrue(Pageable pageable);

  Long countAllByStatusIsTrue();

  @Query("SELECT pid.packagee FROM PackageInDay pid WHERE pid.packagee.id = :packageId ")
  Package findPackageByPackageId(@Param("packageId") Long packageId);
}
