package com.example.emergencyassistb4b4.alert.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DisasterType {

    EARTHQUAKE("지진"),
    FLOOD("홍수"),
    TYPHOON("태풍"),
    WILDFIRE("산불"),
    LANDSLIDE("산사태"),
    POWER_OUTAGE("정전"),
    TERROR_ATTACK("테러"),
    BUILDING_COLLAPSE("건물 붕괴");

    private final String name;

    public static DisasterType from(String value) {
        return DisasterType.valueOf(value.toUpperCase());
    }
}
