package com.example.emergencyassistb4b4.alert.dto.volunteer;

import com.example.emergencyassistb4b4.alert.domain.volunteer.VolunteerAlert;
import com.example.emergencyassistb4b4.volunteer.domain.Post;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VolunteerUpdateAlertDto {

    private final String title;
    private final String location;
    private final LocalDateTime startTime;

    public static VolunteerUpdateAlertDto fromPost(Post post) {

        return VolunteerUpdateAlertDto.builder()
            .title(post.getTitle())
//            .location(post.getLocation().getPlaceName())
//            .startTime(post.getAttendancePolicy().getCheckinStart())
            .build();
    }

    public VolunteerAlert toEntity() {
        return VolunteerAlert.builder()
            .title(this.title)
            .location(this.location)
            .startTime(this.startTime)
            .build();
    }
}
