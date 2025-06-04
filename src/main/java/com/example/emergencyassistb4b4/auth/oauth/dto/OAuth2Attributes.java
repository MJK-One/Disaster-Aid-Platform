package com.example.emergencyassistb4b4.auth.oauth.dto;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
@Getter
public class OAuth2Attributes {
    private final String name;
    private final String email;
    private final Map<String, Object> attributes;
    private final String provider;
    private final String nameAttributeKey;


    public OAuth2Attributes(String name, String email, String provider, String nameAttributeKey, Map<String, Object> attributes) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    public static OAuth2Attributes of(String provider, Map<String, Object> attributes) {
        return switch (provider.toLowerCase()) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new ApiException(ErrorStatus.INVALID_REQUEST);
        };

    }

    public static OAuth2Attributes ofGoogle(Map<String, Object> attributes) {
        return new OAuth2Attributes(
                (String) attributes.get("name"),
                (String) attributes.get("email"),
                "google",
                "email",
                attributes
        );
    }

    public static OAuth2Attributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return new OAuth2Attributes(
                (String) profile.get("nickname"),
                (String) kakaoAccount.get("email"),
                "kakao",
                "email",
                attributes
        );
    }
}
