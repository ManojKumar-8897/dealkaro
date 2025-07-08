package com.digiquad.dealkaro.repository;


import com.digiquad.dealkaro.entity.DeviceSession;
import com.digiquad.dealkaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceSessionRepository extends JpaRepository<DeviceSession, UUID> {
    Optional<DeviceSession> findByUserAndDeviceId(User user, UUID deviceId);
    List<DeviceSession> findAllByUser(User user);
}