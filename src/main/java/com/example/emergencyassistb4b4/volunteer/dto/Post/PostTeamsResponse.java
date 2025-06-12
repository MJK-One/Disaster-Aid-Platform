package com.example.emergencyassistb4b4.volunteer.dto.Post;

import com.example.emergencyassistb4b4.volunteer.domain.VolunteerTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTeamsResponse {

    private List<TeamInfo> teams;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamInfo {
        private int teamNumber;
        private int currentCount;
        private int maxCapacity;

        public static TeamInfo from(VolunteerTeam team, int currentCount) {
            return TeamInfo.builder()
                    .teamNumber(team.getTeamNumber())
                    .currentCount(currentCount)
                    .maxCapacity(team.getMaxCapacity())
                    .build();
        }
    }
}
