package com.example.emergencyassistb4b4.location.controller;

import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.location.dto.request.CoordinateRequestDto;
import com.example.emergencyassistb4b4.location.dto.request.RegionRequestDto;
import com.example.emergencyassistb4b4.location.redis.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.emergencyassistb4b4.global.status.SuccessStatus.LOCATION_SAVE_SUCCESS;


// 백그라운드 존재, 입력받을 창구로써 역할 -> return 값이 Void로 변경가능성 존재
// 일단 JWT에서 userId 가져올 예정
@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // 모든 유저 저장
    @PostMapping("/region")
    public ResponseEntity<ApiResponse<String>> saveRegion(@RequestBody RegionRequestDto dto) {
        locationService.saveRegion(dto.getUserId(), dto.getSi(), dto.getGu());
        return ApiResponse.onSuccess(LOCATION_SAVE_SUCCESS,"시/구 정보 저장 완료");
    }

    //봉사자만 저장
    @PostMapping("/coordinates")
    public ResponseEntity<ApiResponse<String>> saveCoordinates(@RequestBody CoordinateRequestDto dto) {
//        if (!locationService.isVolunteer(dto.getUserId())) {
//            throw new ForbiddenException(ErrorStatus.USER_NOT_VOLUNTEER);
//        }
        locationService.saveCoordinates(dto.getUserId(), dto.getLatitude(), dto.getLongitude());
        return ApiResponse.onSuccess(LOCATION_SAVE_SUCCESS,"위치 좌표 저장 완료");
    }


}

