package com.digiquad.dealkaro.model.DTO;


import com.digiquad.dealkaro.entity.DeviceSession;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MySessionsResponseDTO {
    private List<DeviceSessionDTO> activeSessions;
    private List<DeviceSessionDTO> loggedOutSessions;

    public static MySessionsResponseDTO from(List<DeviceSession> sessions) {
        List<DeviceSessionDTO> active = new ArrayList<>();
        List<DeviceSessionDTO> loggedOut = new ArrayList<>();
        for (DeviceSession session : sessions) {
            DeviceSessionDTO dto = DeviceSessionDTO.from(session);
            if (session.getLogoutTime() == null) active.add(dto);
            else loggedOut.add(dto);
        }
        return MySessionsResponseDTO.builder()
                .activeSessions(active)
                .loggedOutSessions(loggedOut)
                .build();
    }
}

