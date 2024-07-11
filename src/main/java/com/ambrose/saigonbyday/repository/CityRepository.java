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
public interface CityRepository extends JpaRepository<City, String> {

  City findById(Long id);
  @Query("SELECT d FROM Destination d WHERE d.city.id = :cityId")
  List<Destination> findDestinationByCityId(@Param("cityId") Long cityId);

  City findByStatusIsTrueAndId(Long id);

  List<City> findAllBy();

  List<City> findAllBy(Pageable pageable);
  List<City> findAllByStatusIsTrue(Pageable pageable);
  Long countAllByStatusIsTrue();


}
