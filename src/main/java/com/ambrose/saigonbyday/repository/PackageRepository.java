package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, String> {
  Package findById(Long id);

}
