package com.example.emergencyassistb4b4.userDevice.repository;

import com.example.emergencyassistb4b4.userDevice.domain.UserDevice;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    List<UserDevice> findByDeviceIdIn(Set<String> deviceIds);

    Optional<UserDevice> findByUserId(Long userId);

    List<UserDevice> findByUserIdIn(List<Long> userIds);
}
