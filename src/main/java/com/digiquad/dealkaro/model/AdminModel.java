package com.digiquad.dealkaro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.digiquad.dealkaro.entity.UserRole;

/**
 * This model is used for accepting requests for updating the admin details
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AdminModel implements Serializable {
    private String id;
    private String userName;
    private String name;
    private String mobileNumber;
    private String email;
    private String profileImageUrl;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean approvalStatus;
    private Boolean isActive;
    private Boolean isDeleted;
    private Boolean isLoggedIn;
    private String adhaarNumber;
    private LocalDateTime updatedAt;
    private UserRole userType;
}