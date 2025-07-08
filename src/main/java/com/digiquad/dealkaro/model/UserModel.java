package com.digiquad.dealkaro.model;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * This Model is used to carry the payload data for user details. Can use for creation, update purpose
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserModel implements Serializable {
//    private String id;
    private String userName;
    private String name;
    private String mobileNumber;
    private String email;
    @Nullable
    private MultipartFile profileImage;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean approvalStatus;
    private Boolean isActive;
    private Boolean isDeleted;
    private Boolean isLoggedIn;
    private String adhaarNumber;
    @NotNull(message = "Password Mandatory...")
    private String password;
}
