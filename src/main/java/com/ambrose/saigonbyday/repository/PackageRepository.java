package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.Package;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, String> {
  Package findById(Long id);

  Package findByStatusIsTrueAndId(Long id);

  List<Package> findAllBy(Pageable pageable);
  List<Package> findAllByStatusIsTrue(Pageable pageable);

  

  Long countAllByStatusIsTrue();
}
