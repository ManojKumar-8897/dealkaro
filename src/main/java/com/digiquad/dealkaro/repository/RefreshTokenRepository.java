package com.digiquad.dealkaro.repository;

import com.digiquad.dealkaro.entity.DeviceSession;
import com.digiquad.dealkaro.entity.RefreshToken;
import com.digiquad.dealkaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByDeviceSession(DeviceSession deviceSession);

    Optional<RefreshToken> findByDeviceSession_UserAndDeviceSession_DeviceId(User user, UUID deviceId);

}