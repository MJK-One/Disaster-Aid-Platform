package com.example.emergencyassistb4b4.user.repository;

import com.example.emergencyassistb4b4.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
