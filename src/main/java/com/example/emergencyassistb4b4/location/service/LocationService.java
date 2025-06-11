package com.example.emergencyassistb4b4.location.service;

import com.example.emergencyassistb4b4.location.redis.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public void saveRegion(Long userId, String si, String gu) {
        locationRepository.saveRegion(userId, si, gu);
    }

    public void saveCoordinates(Long userId, double latitude, double longitude) {
        locationRepository.saveCoordinates(userId, latitude, longitude);
    }

    public List<Object> getRegion(String region) {
        return locationRepository.getRegionUsers(region);
    }

    public Optional<Point> getCoordinates(Long userId) {
        return locationRepository.getCoordinates(userId.toString());
    }

    public List<Object> findUsersWithinRadius(double latitude, double longitude, double radiusMeters) {
        return locationRepository.findUsersWithinRadius(latitude, longitude, (int) radiusMeters);
    }
}