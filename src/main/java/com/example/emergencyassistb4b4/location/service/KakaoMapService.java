package com.example.emergencyassistb4b4.location.service;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.location.dto.response.DisasterSummaryDto;
import com.example.emergencyassistb4b4.location.dto.response.ShelterResponseDto;
import com.example.emergencyassistb4b4.location.util.KakaoApiUtils;
import com.example.emergencyassistb4b4.report.enums.ReportStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class KakaoMapService {

    @Value("${kakao.api.key}")
    private String restApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
//    private final ReportRepository reportRepository;

    public List<ShelterResponseDto> searchShelters(double latitude, double longitude, double radiusMeter){
        String categoryCode = "PO3"; // 치안기관
        String url = KakaoApiUtils.buildCategorySearchUrl(categoryCode, longitude, latitude, radiusMeter);

        HttpHeaders headers = KakaoApiUtils.createAuthHeader(restApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new ApiException(ErrorStatus.KAKAO_API_REQUEST_FAILED);
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");

            List<ShelterResponseDto> shelters = new ArrayList<>();
            for (int i = 0; i < Math.min(3, documents.size()); i++) {
                shelters.add(ShelterResponseDto.from(documents.get(i)));
            }

            return shelters;

        } catch (JsonProcessingException e) {
            throw new ApiException(ErrorStatus.KAKAO_API_RESPONSE_PARSE_FAILED);
        } catch (RestClientException e) {
            throw new ApiException(ErrorStatus.KAKAO_API_RESPONSE_STATUS_ERROR);
        }

    }


    public List<DisasterSummaryDto> getDisasterSummary(
            double latitude,
            double longitude,
            int radiusMeter,
            Duration withinTime
    ) {
        LocalDateTime fromTime = LocalDateTime.now().minus(withinTime);

        // 예시용 mock 데이터. 실제로는 repository 에서 가져올 것
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{DisasterType.TYPHOON, ReportStatus.CLOSED, 5L, 37.1234, 127.1234});
        results.add(new Object[]{DisasterType.FLOOD, ReportStatus.PENDING, 2L, 37.1256, 127.1212});
        results.add(new Object[]{DisasterType.EARTHQUAKE, ReportStatus.RECEIVED, 1L, 37.1211, 127.1266});


//        List<Object[]> results = reportRepository.findDisasterSummaryWithLocation(
//              latitude, longitude, count ,radiusMeter, fromTime);


        return results.stream()
                .map(DisasterSummaryDto::from)
                .toList();
    }

}
