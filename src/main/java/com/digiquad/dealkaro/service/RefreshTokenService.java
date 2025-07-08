package com.digiquad.dealkaro.service;


import com.digiquad.dealkaro.entity.RefreshToken;
import com.digiquad.dealkaro.entity.User;
import com.digiquad.dealkaro.model.DTO.DeviceSessionDTO;
import com.digiquad.dealkaro.model.DTO.MySessionsResponseDTO;
import com.digiquad.dealkaro.model.DTO.RefreshTokenRequestDTO;

import java.util.UUID;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user, DeviceSessionDTO dto);
    boolean validateRefreshToken(String token);
    User getUserFromRefreshToken(String token);
    void logoutFromDevice(User user, String deviceId);
    void logoutFromAllDevices(User user);
    MySessionsResponseDTO getUserSessions(User user);
}
