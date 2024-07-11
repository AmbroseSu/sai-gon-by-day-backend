package com.ambrose.saigonbyday.repository;


import com.ambrose.saigonbyday.entities.Destination;
import com.ambrose.saigonbyday.entities.Package;
import com.ambrose.saigonbyday.entities.PackageInDestination;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PackageInDestinationRepository extends JpaRepository<PackageInDestination, String> {
  @Transactional
  Integer deleteAllByDestinationId(Long destinationId);

  @Transactional
  Integer deleteAllByPackageeId(Long packageId);

  List<PackageInDestination> findAllBy(Pageable pageable);
  PackageInDestination findById(Long id);

  @Query("SELECT pa FROM Package pa "+
      "JOIN PackageInDestination pid ON pa.id = pid.packagee.id " +
      "WHERE pid.destination.id = :destinationId")
  List<Package> findPackagesByDestinationId(@Param("destinationId") Long destinationId);

  @Query("SELECT de FROM Destination de "
      + "JOIN PackageInDestination pid ON de.id = pid.destination.id "
      + "WHERE pid.packagee.id = :packageId")
  List<Destination> findDestinationsByPackageId(@Param("packageId") Long packageId);
}
