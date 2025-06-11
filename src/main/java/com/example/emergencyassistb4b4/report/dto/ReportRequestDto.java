package com.example.emergencyassistb4b4.report.dto;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.report.domain.Report;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Getter
@Builder
@AllArgsConstructor
public class ReportRequestDto {

    @NotNull(message = "재난 유형은 필수입니다.")
    private final DisasterType disasterType;

    @NotBlank(message = "상세 설명은 필수입니다.")
    @Size(max = 1000, message = "설명은 최대 1000자까지 입력 가능합니다.")
    private final String description;

    @Size(max = 2048, message = "이미지 URL은 최대 2048자까지 가능합니다.")
    private final String imageUrl;

    @Size(max = 2048, message = "동영상 URL은 최대 2048자까지 가능합니다.")
    private final String videoUrl;
}
