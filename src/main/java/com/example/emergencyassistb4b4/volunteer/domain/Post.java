package com.example.emergencyassistb4b4.volunteer.domain;

import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.volunteer.enums.PostCateGory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name="Post")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Enumerated(EnumType.STRING)
    private PostCateGory category;

    private String title;

    @Lob
    private String content;

    private int totalCapacity;

    private int teamSize;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private VolunteerLocation volunteerLocation;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_policy_id")
    private AttendancePolicy attendancePolicy;
}