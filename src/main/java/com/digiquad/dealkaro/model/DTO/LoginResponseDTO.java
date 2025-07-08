package com.digiquad.dealkaro.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String userId;
    private String name;
    private String email;
    private String responseMessage;
    private String profilePhoto;
    private boolean newUser;
    private boolean hasPassword;
    private Instant refreshExpiresAt;
    private String tokenType = "Bearer";
}