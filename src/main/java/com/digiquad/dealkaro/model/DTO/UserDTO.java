package com.digiquad.dealkaro.model.DTO;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO to send user data in API responses (Admin, Super Admin, Normal User)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO implements Serializable {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserRoleDTO userRole;
}
