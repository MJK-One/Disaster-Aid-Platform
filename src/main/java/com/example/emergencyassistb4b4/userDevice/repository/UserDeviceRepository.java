package com.example.emergencyassistb4b4.userDevice.repository;

import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.userDevice.domain.UserDevice;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    Optional<UserDevice> findByUser(User user);

    @Query("SELECT ud FROM UserDevice ud WHERE ud.user.id IN :userIds")
    List<UserDevice> findByUserIdIn(List<Long> userIds);
}
