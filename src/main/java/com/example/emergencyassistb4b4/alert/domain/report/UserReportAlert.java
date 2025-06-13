package com.example.emergencyassistb4b4.alert.domain.report;

import com.example.emergencyassistb4b4.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "user_report_alert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SequenceGenerator(
    name = "user_report_alert_seq_gen",
    sequenceName = "user_report_alert_seq",
    allocationSize = 50
)
public class UserReportAlert {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "user_report_alert_seq_gen"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private ReportAlert reportAlert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static List<UserReportAlert> fromUsers(ReportAlert alert, List<User> users) {
        return users.stream()
            .map(user -> UserReportAlert.builder()
                .user(user)
                .reportAlert(alert)
                .build())
            .toList();
    }
}
