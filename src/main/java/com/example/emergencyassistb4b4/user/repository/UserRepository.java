package com.example.emergencyassistb4b4.user.repository;

import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findFirstByProvinceStartingWithAndUserRole(String province, UserRole role);

    // 예: city 와 role 기준 조회
    Optional<User> findFirstByProvinceAndCityAndUserRole(String province, String city, UserRole role);

    // 또는 province 만으로 조회
    Optional<User> findFirstByProvinceAndUserRole(String province, UserRole role);

}