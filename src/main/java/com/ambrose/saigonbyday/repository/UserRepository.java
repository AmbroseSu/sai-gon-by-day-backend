package com.ambrose.saigonbyday.repository;

import com.ambrose.saigonbyday.entities.Gallery;
import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.entities.enums.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
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
  List<User> findAllBy(Pageable pageable);

  boolean existsByPhone(String phone);
  User findByRole(Role role);

  List<User> findByRole(Role role, Pageable pageable);

  @Query("SELECT us FROM User us WHERE us.email LIKE :email")
  User findUserByEmail(String email);

  @Query("SELECT us FROM User us WHERE us.id = :id")
  User findUserById(Long id);
  @Query("SELECT us FROM User us WHERE us.phone LIKE :phone")
  User findUserByPhone(String phone);
  Long countAllByIsEnabledIsTrue();
}
