package com.example.emergencyassistb4b4.volunteer.repository;

import com.example.emergencyassistb4b4.volunteer.domain.VolunteerTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerTeamRepository extends JpaRepository<VolunteerTeam, Long> {

    Optional<VolunteerTeam> findByPost_IdAndTeamNumber(Long postId, int teamNumber);

}
