package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

  VerificationToken findByToken(String token);
  @Query("SELECT v FROM VerificationToken v WHERE v.user.id =:id")
  VerificationToken findByUserId(Long id);
}
