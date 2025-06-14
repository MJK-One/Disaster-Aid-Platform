package com.example.emergencyassistb4b4.report.dto;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 위도,경도,시,구를 프론트에서 가져오기(일회성이라서)
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

    @NotNull(message = "위도는 필수입니다.")
    private final Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    private final Double longitude;

    @NotBlank(message = "시 정보는 필수입니다.")
    private final String si;

    @NotBlank(message = "구 정보는 필수입니다.")
    private final String gu;
}
