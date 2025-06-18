package com.example.emergencyassistb4b4.user.repository;

import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findFirstByNicknameStartingWithAndUserRole(String Si, UserRole role);

    // UserRole.GOV, nicknamePrefix = "서울시" -> "서울시119" 유저 반환
    @Query("SELECT u.id FROM User u WHERE u.userRole = :role AND u.nickname LIKE CONCAT(:si, '%')")
    List<Long> findIdsByRoleAndNicknameStartingWith(@Param("role") UserRole role, @Param("si") String si);

    Optional<User> findFirstByUserRoleAndNicknameContaining(UserRole role, String nicknameFragment);
}
