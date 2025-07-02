package com.example.emergencyassistb4b4.global.util;

public class RegionUtils {

    // 생성자 private -> 유팅 클래스 인스턴스화 방지
    private RegionUtils() {}

    /**
     * 행정구역 이름 정규화
     * @param region1 region_1depth_name (ex. 경기도, 서울특별시)
     * @param region2 region_2depth_name (ex. 광주시, 강남구)
     * @return 정규화된 시 정보 문자열 (ex. 경기도 광주시)
     */
    public static String normalizeSi(String region1, String region2) {

        if (region1 == null) return "";

        if (region2 == null || region2.isBlank()) return region1.trim();

        return region1.trim() + " " +  region2.trim();
    }

    /**
     * 이미 결합된 시 정보를 정리 (공백 정리 등)
     * @param siRaw "경기도  광주시" 등
     * @return "경기도 광주시"
     */
    public static String normalizeSi(String siRaw) {
        return siRaw == null ? "" : siRaw.trim().replaceAll("\\s+", " ");
    }
}
