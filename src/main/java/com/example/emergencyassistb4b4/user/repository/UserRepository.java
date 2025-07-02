package com.example.emergencyassistb4b4.user.repository;

import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findFirstBySiStartingWithAndUserRole(String Si, UserRole role);

    Optional<User> findById(Long id);

    // 지역명과 역할로 정확히 매칭되는 공공기관 유저 검색
    Optional<User> findFirstByUserRoleAndSi(UserRole userRole, String si);
}