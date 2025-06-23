package com.example.emergencyassistb4b4.userDevice.enums;

public enum DeviceType {
    PHONE,
    TABLET,
    DESKTOP,
    UNKNOWN;

    public static DeviceType from(String value) {
        return DeviceType.valueOf(value.toUpperCase());
    }
}
