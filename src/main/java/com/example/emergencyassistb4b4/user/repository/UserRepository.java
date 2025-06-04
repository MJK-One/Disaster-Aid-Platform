package com.example.emergencyassistb4b4.user.repository;

import com.example.emergencyassistb4b4.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);
}
