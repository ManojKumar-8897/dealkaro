package com.digiquad.dealkaro.model.DTO;


import com.digiquad.dealkaro.entity.DeviceSession;
import com.digiquad.dealkaro.entity.RefreshToken;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSessionDTO {
    private UUID deviceId;
    private String deviceName;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String status;
    private TokenInfo token;

    public static DeviceSessionDTO from(DeviceSession session) {
        return DeviceSessionDTO.builder()
                .deviceId(session.getDeviceId())
                .deviceName(session.getDeviceName())
                .ipAddress(session.getIpAddress())
                .userAgent(session.getUserAgent())
                .loginTime(session.getLoginTime())
                .logoutTime(session.getLogoutTime())
                .status(session.getIsActive()? "Active" : "De-Activate")
                .token(null) // populate if needed
                .build();
    }
}

