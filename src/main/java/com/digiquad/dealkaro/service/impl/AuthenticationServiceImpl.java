package com.digiquad.dealkaro.service.impl;

import com.digiquad.dealkaro.entity.User;
import com.digiquad.dealkaro.exceptions.customExceptions.auth.InvalidLoginException;
import com.digiquad.dealkaro.filter.JwtFilter;
import com.digiquad.dealkaro.model.DTO.DeviceSessionDTO;
import com.digiquad.dealkaro.model.DTO.LoginDTO;
import com.digiquad.dealkaro.model.DTO.LoginResponseDTO;
import com.digiquad.dealkaro.model.DTO.RefreshTokenRequestDTO;
import com.digiquad.dealkaro.repository.UserRepository;
import com.digiquad.dealkaro.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtServiceImpl  jwtServiceImpl ;
    private final RefreshTokenServiceImpl refreshTokenService;


    @Override
    @Transactional
    public LoginResponseDTO authenticateUser(LoginDTO loginRequest, DeviceSessionDTO dto) {

        LoginResponseDTO loginResponse = new LoginResponseDTO();

        String inputKey = loginRequest.getInputKey();
        String password = loginRequest.getPassword();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(inputKey, password)
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidLoginException("Invalid username/email/mobile or password.");
        }

        User user = userRepository.findByMobileNumber(inputKey)
                .orElseGet(() -> userRepository.findByUserName(inputKey)
                        .orElseGet(() -> userRepository.findByEmail(inputKey)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                        "User not found by given ID, Mobile or Email ID: "))));

        String role = user.getUserType().getName();

        String token = jwtServiceImpl.generateToken(user.getUserName(), List.of(role), dto.getDeviceId());

        String refreshToken = refreshTokenService.createRefreshToken(user, dto).getToken();

        if ((user.getApprovalStatus() && Boolean.TRUE.equals(!user.getIsDeleted()))) {
            loginResponse.setUserId(user.getId().toString());
            loginResponse.setName(user.getName());
            loginResponse.setAccessToken(token);
            loginResponse.setHasPassword(user.getHasPassword());
            loginResponse.setRefreshToken(refreshToken);
        } else {
            loginResponse.setResponseMessage("Approval Process Pending or Account Disabled !!!");
        }

        return loginResponse;
    }
}

