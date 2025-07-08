package com.digiquad.dealkaro.service;

import com.digiquad.dealkaro.model.DTO.DeviceSessionDTO;
import com.digiquad.dealkaro.model.DTO.LoginDTO;
import com.digiquad.dealkaro.model.DTO.LoginResponseDTO;
import com.digiquad.dealkaro.model.DTO.RefreshTokenRequestDTO;

public interface AuthenticationService {
    LoginResponseDTO authenticateUser(LoginDTO loginRequest, DeviceSessionDTO dto);
}
