package com.example.emergencyassistb4b4.volunteer.repository;

import com.example.emergencyassistb4b4.volunteer.domain.VolunteerParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VolunteerParticipantRepository extends JpaRepository<VolunteerParticipant, Long> {
    @Query("""
        SELECT vp.user.id
        FROM VolunteerParticipant vp
        JOIN vp.volunteerTeam t
        WHERE t.post.id = :postId
    """)
    List<Long> findUserIdsByPostId(@Param("postId") Long postId);
    @Query("""
    SELECT vp
    FROM VolunteerParticipant vp
    JOIN FETCH vp.volunteerTeam t
    JOIN FETCH t.post p
    JOIN FETCH p.location l
    JOIN FETCH p.attendancePolicy ap
    WHERE vp.id = :volunteerId
""")
    Optional<VolunteerParticipant> findWithTeamAndPolicyById(@Param("volunteerId") Long volunteerId);
}
