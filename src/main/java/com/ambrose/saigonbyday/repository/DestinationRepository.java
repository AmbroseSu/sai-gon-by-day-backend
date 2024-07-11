package com.ambrose.saigonbyday.repository;


import com.ambrose.saigonbyday.entities.City;
import com.ambrose.saigonbyday.entities.Destination;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, String> {

  Destination findById(Long id);
  List<Destination> findAllBy(Pageable pageable);
  List<Destination> findAllByCityId(Long cityId);
  Destination findByStatusIsTrueAndId(Long id);
  List<Destination> findAllByStatusIsTrue(Pageable pageable);
  Long countAllByStatusIsTrue();

  @Query("SELECT d.city FROM Destination d WHERE d.city.id = :cityId ")
  City findCityByCityId(@Param("cityId") Long cityId);
}
