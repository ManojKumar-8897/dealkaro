package com.digiquad.dealkaro.model.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import com.digiquad.dealkaro.entity.User;
import com.digiquad.dealkaro.entity.UserRole;

/**
 * This model is used for sending admin details as response
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AdminDTO {
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
    private UserRoleDTO userType;
    private User approvedBy;

    /**
     * This constructor is used for sending the response of view Admin Details purpose.
     * Note: You can reuse but don't modify
     */
    public AdminDTO(String id, String userName, String name, String mobileNumber, String email, String profileImageUrl,
                    Boolean isActive, Boolean isDeleted, String adhaarNumber, UserRoleDTO userType) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.adhaarNumber = adhaarNumber;
        this.userType = userType;
    }
}
