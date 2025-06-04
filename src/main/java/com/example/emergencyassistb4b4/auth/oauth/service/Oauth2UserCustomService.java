package com.example.emergencyassistb4b4.auth.oauth.service;

import com.example.emergencyassistb4b4.auth.dto.LoginResponse;
import com.example.emergencyassistb4b4.auth.oauth.dto.OAuth2Attributes;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class Oauth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        //google or kakao
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(registrationId, attributes);
        saveOrUpdate(oAuth2Attributes);
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_IND")),
                oAuth2Attributes.getAttributes(),
                "email"
        );
    }

    private User saveOrUpdate(OAuth2Attributes oAuth2Attributes) {
        return userRepository.findByEmail(oAuth2Attributes.getEmail())
                .map(user -> user.updateNickname(user.getNickname()))
                .orElseGet(()-> userRepository.save(
                        User.builder()
                                .email(oAuth2Attributes.getEmail())
                                .nickname(oAuth2Attributes.getName())
                                .userRole(UserRole.IND)
                                .build()
                ));
    }
}
