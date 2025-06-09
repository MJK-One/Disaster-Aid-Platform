package com.example.emergencyassistb4b4.auth.oauth.service;

import com.example.emergencyassistb4b4.auth.oauth.dto.OAuth2Attributes;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * OAuth2 인증 후 사용자 정보를 처리하는 서비스
 * Spring Security의 DefaultOAuth2UserService를 확장하여 사용자 정보를 로드하고,
 * 시스템의 사용자 엔티티와 연결하며 (회원가입 또는 정보 업데이트),
 * 최종적으로 Spring Security가 관리할 수 있는 OAuth2User 객체를 반환합니다.
 */
@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 DefaultOAuth2UserService의 loadUser 메서드를 호출하여
        // Provider로부터 사용자 정보를 가져와 OAuth2User 객체로 반환받는다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 어떤 Provider (google, kakao 등)에서 왔는지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // Provider로부터 받은 원본 사용자 속성(정보) 맵을 가져옴
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 가져온 Provider 이름과 속성 맵을 사용하여
        // 이전에 정의한 OAuth2Attributes 객체로 변환
        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(registrationId, attributes);


        // (회원가입 또는 로그인 시 기존 사용자 정보 업데이트)
        saveOrUpdate(oAuth2Attributes);

        // 첫 번째 인자: 사용자에게 부여할 권한 목록 (현재는 "ROLE_IND" 고정)
        // 두 번째 인자: OAuth2 Provider로부터 가져온 원본 속성 맵 (또는 공통화된 정보)
        // 세 번째 인자: 사용자 식별에 사용할 속성의 키 이름 (여기서는 "email"로 설정됨)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_IND")),
                oAuth2Attributes.getAttributes(),
                oAuth2Attributes.getProviderId()

        );
    }

    /**
     * OAuth2 사용자 정보(OAuth2Attributes)를 바탕으로 시스템에 사용자 정보를 저장하거나 업데이트함
     * 이메일을 기준으로 기존 사용자를 찾고, 존재하면 닉네임을 업데이트하고,
     * 존재하지 않으면 새로운 사용자로 등록한다
     *
     * @param oAuth2Attributes 변환된 OAuth2 사용자 정보 객체
     * @return 저장되거나 업데이트된 User 엔티티 객체
     */
    private User saveOrUpdate(OAuth2Attributes oAuth2Attributes) {
        return userRepository.findByEmail(oAuth2Attributes.getEmail())
                .map(user -> user.updateNickname(oAuth2Attributes.getName()))
                .orElseGet(()-> userRepository.save(
                        User.builder()
                                .email(oAuth2Attributes.getEmail())
                                .nickname(oAuth2Attributes.getName())
                                .userRole(UserRole.IND)
                                .build()
                ));
    }
}
