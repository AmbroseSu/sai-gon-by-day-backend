package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.entities.enums.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  //Optional<User> findByEmail(String email);

  @Query("SELECT us FROM User us WHERE us.login LIKE :login")
  Optional<User> findByLogin(String login);
  Optional<User> findByPhone(String phone);
  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);
  User findByRole(Role role);

  @Query("SELECT us FROM User us WHERE us.email LIKE :email")
  User findUserByEmail(String email);

  @Query("SELECT us FROM User us WHERE us.id = :id")
  User findUserById(Long id);
  @Query("SELECT us FROM User us WHERE us.phone LIKE :phone")
  User findUserByPhone(String phone);
}
