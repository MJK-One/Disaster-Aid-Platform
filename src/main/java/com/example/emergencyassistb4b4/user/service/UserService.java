package com.example.emergencyassistb4b4.user.service;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.report.repository.ReportRepository;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.UserRequestDto;
import com.example.emergencyassistb4b4.user.dto.UserResponseDto;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public UserResponseDto getMyInfo(UserRequestDto userRequestDto) {
        User user = userRepository.findByEmail(userRequestDto.getEmail())
                .orElseThrow( () -> new ApiException(ErrorStatus.USER_NOT_FOUND));
        return UserResponseDto.from(user);
    }

    @Transactional(readOnly = true)
    public User getReporterInfo(Long reportId, User responder) {

        // 신고 조회
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ApiException(ErrorStatus.REPORT_NOT_FOUND));

        // 권한 확인 (해당 신고의 담당자인지 확인)
        if (!report.getResponder().getId().equals(responder.getId())) {
            throw new ApiException(ErrorStatus.CUSTOM_ERROR_STATUS);
        }

        // 신고자 반환
        return report.getReporter();
    }

    // 공공기관 조회
    public User findGovernment(String si) {
        String keyword = si.replace("특별시", "").replace("광역시", "").replace("자치시", "").replace("도", "");
        return userRepository.findFirstBySiStartingWithAndUserRole(keyword, UserRole.GOV)
            .orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));
    }
}
