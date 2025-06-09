package com.example.emergencyassistb4b4.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 * HTTP 쿠키 관련 작업을 수행하는 유틸리티 클래스입니다.
 * 쿠키 추가, 삭제, 객체 직렬화/역직렬화 기능을 제공합니다.
 */
public class CookieUtil {
    //요청 값, 이름 만료기간을 바탕으로 쿠키 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        response.addCookie(cookie);

    }

    //쿠키의 이름을 입력받아 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if(name.equals(cookie.getName())) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                cookie.setValue("");
                response.addCookie(cookie);
            }
        }

    }
    // 객체를 직렬화해서 쿠키의 값으로 변환
    public static String serialize(Object obj) {
        if (!(obj instanceof Serializable)) {
            throw new IllegalArgumentException("객체는 Serializable을 구현해야 합니다.");
        }
        byte[] data = SerializationUtils.serialize(obj);
        return Base64.getUrlEncoder().encodeToString(data);
    }
    //역직렬화해서 객체로 변환
    public static Object deserialize(Cookie cookie, Class<?> cls) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getUrlDecoder().decode(cookie.getValue()));
             ObjectInputStream ois = new ObjectInputStream(bais)) {

            Object obj = ois.readObject();
            return cls.cast(obj);

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("역직렬화 중 오류 발생", e);
        }

    }
}