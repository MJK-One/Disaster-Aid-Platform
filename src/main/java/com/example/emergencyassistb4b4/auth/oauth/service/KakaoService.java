package com.example.emergencyassistb4b4.auth.oauth.service;

import com.example.emergencyassistb4b4.auth.oauth.dto.KakaoUserDetailsDto;
import com.google.api.client.http.HttpHeaders;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Service

public class KakaoService {
    // 카카오 사용자 정보를 가져올 API URL
    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate;



    public KakaoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * 카카오 액세스 토큰을 사용하여 사용자 정보를 가져오는 메서드
     * @param accessToken 카카오 OAuth2 액세스 토큰
     * @return KakaoUserDetails 카카오 사용자 정보
     */
    public KakaoUserDetailsDto getKakaoUserInfo(String accessToken) {
        // 요청 헤더에 카카오 액세스 토큰을 포함
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);  // Bearer 방식을 사용해 인증
        // HTTP GET 요청을 보낼 HttpEntity 객체 생성
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);

        // 카카오 API 호출(사용자 정보 요청)
        ResponseEntity<Map> response = restTemplate.exchange(KAKAO_USER_INFO_URL, HttpMethod.GET, entity, Map.class);

        // 응답에서 사용자 정보를 파싱하여 kakaoUserDetailsDto 객체로 변환
        return parseKakaoUserDetails(response.getBody());
    }
    /**
     * 카카오 사용자 정보를 파싱하여 KakaoUserDetails 객체로 변환하는 메서드
     * @param attributes 카카오 API에서 받은 사용자 정보
     * @return KakaoUserDetails 객체
     */
    private KakaoUserDetailsDto parseKakaoUserDetails(Map<String, Object> attributes) {
        Long kakaoId = (Long) attributes.get("id");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account"); //이메일 포함
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        KakaoUserDetailsDto kakaoUserDetailsDto = new KakaoUserDetailsDto();
        kakaoUserDetailsDto.setId(kakaoId);
        kakaoUserDetailsDto.setNickname((String) profile.get("nickname"));
        kakaoUserDetailsDto.setEmail((String) profile.get("email"));

        return kakaoUserDetailsDto;
    }
}
