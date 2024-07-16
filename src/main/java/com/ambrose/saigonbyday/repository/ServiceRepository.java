package com.ambrose.saigonbyday.repository;


import com.ambrose.saigonbyday.entities.Destination;
import com.ambrose.saigonbyday.entities.Servicee;
import com.ambrose.saigonbyday.entities.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Servicee, String> {
  Servicee findById(Long id);

  //List<Servicee> findAllByDestinationId(Long cityId);

  List<Servicee> findAllBy(Pageable pageable);
  @Query("SELECT s.user FROM Servicee s WHERE s.user.id = :userId")
  User findUserByUserId(@Param("userId") Long userId);

  Servicee findByStatusIsTrueAndId(Long id);

  List<Servicee> findAllByStatusIsTrue(Pageable pageable);


  Long countAllByStatusIsTrue();
}
