package com.digiquad.dealkaro.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.digiquad.dealkaro.model.DTO.DeviceSessionDTO;
import com.digiquad.dealkaro.model.DTO.RefreshTokenRequestDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.digiquad.dealkaro.entity.RefreshToken;
import com.digiquad.dealkaro.entity.User;
import com.digiquad.dealkaro.entity.UserRole;
import com.digiquad.dealkaro.exceptions.customExceptions.UserRoleNotFoundException;
import com.digiquad.dealkaro.model.DTO.LoginResponseDTO;
import com.digiquad.dealkaro.repository.UserRepository;
import com.digiquad.dealkaro.repository.UserRoleRepository;
import com.digiquad.dealkaro.service.impl.JwtServiceImpl;
import com.digiquad.dealkaro.service.impl.RefreshTokenServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtServiceImpl jwtService;
    private final RefreshTokenServiceImpl refreshTokenService;

    // Password encoder
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String name = oauthUser.getAttribute("name");
        String email = oauthUser.getAttribute("email");
        String picture = oauthUser.getAttribute("picture");
        String userName = email != null ? email.split("@")[0] : null;

        boolean isNewUser = false;
        boolean hasPassword = false;

        Optional<User> userOpt = userRepository.findByUserName(userName);
        if (!userOpt.isPresent()) {
            userOpt = userRepository.findByEmail(email);
        }

        User user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
            hasPassword = user.getHasPassword();
        } else {
            try {
                UserRole userRole = userRoleRepository.findById(2)
                        .orElseThrow(() -> new UserRoleNotFoundException("UserRole with ID 2 not found"));

                user = User.builder()
                        .userName(userName)
                        .name(name)
                        .email(email)
                        .userType(userRole)
                        .mobileNumber(generateMobileNumber())
                        .adhaarNumber(null)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .hasPassword(false)
                        .approvalStatus(true)
                        .isActive(true)
                        .isDeleted(false)
                        .profileImageUrl(picture)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                user = userRepository.save(user);
                isNewUser = true;

            } catch (Exception e) {
                sendJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        errorMessage("Error saving user"));
                return;
            }
        }

        if (user.getApprovalStatus() && !user.getIsDeleted()) {
            String role = user.getUserType().getName();

            // Build device session info
            DeviceSessionDTO deviceInfo = buildDeviceInfo(request);

            // Generate tokens
       String accessToken = jwtService.generateToken(user.getUserName(), List.of(role),deviceInfo.getDeviceId());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, deviceInfo);

            // Build login response
            LoginResponseDTO loginResponse = new LoginResponseDTO();
            loginResponse.setUserId(user.getId().toString());
            loginResponse.setName(user.getName());
            loginResponse.setEmail(user.getEmail());
            loginResponse.setProfilePhoto(user.getProfileImageUrl());
        loginResponse.setAccessToken(accessToken);
            loginResponse.setRefreshToken(refreshToken.getToken());
            loginResponse.setNewUser(isNewUser);
            loginResponse.setHasPassword(hasPassword);
            loginResponse.setResponseMessage(isNewUser ? "Registration successful" : "Login successful");

            int successStatus = isNewUser ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_OK;
            sendJson(response, successStatus, loginResponse);

        } else {
            sendJson(response, HttpServletResponse.SC_FORBIDDEN,
                    errorMessage("Approval pending or account disabled"));
        }
    }

    private DeviceSessionDTO buildDeviceInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        String ipAddress = session != null && session.getAttribute("X-Forwarded-For") != null
                ? (String) session.getAttribute("X-Forwarded-For")
                : request.getRemoteAddr();

        String deviceIdStr = session != null ? (String) session.getAttribute("X-Device-Id") : null;
        String deviceName = session != null ? (String) session.getAttribute("X-Device-Name") : null;
        String userAgent = session != null ? (String) session.getAttribute("User-Agent") : request.getHeader("User-Agent");

        UUID deviceId = deviceIdStr != null
                ? UUID.fromString(deviceIdStr)
                : UUID.nameUUIDFromBytes((userAgent + ipAddress).getBytes());

        return DeviceSessionDTO.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }


    private UUID generateDeviceId(String userAgent, String ipAddress) {
        return UUID.nameUUIDFromBytes((userAgent + ipAddress).getBytes());
    }

    private void sendJson(HttpServletResponse response, int statusCode, Object data) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        new com.fasterxml.jackson.databind.ObjectMapper().writeValue(response.getWriter(), data);
    }

    private LoginResponseDTO errorMessage(String message) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setResponseMessage(message);
        return dto;
    }

    public static String generateMobileNumber() {
        int firstDigit = 6 + (int) (Math.random() * 4);
        long remainingDigits = (long) (Math.random() * 1_000_000_000L);
        return firstDigit + String.format("%09d", remainingDigits);
    }
}
