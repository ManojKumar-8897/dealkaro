package com.digiquad.dealkaro.model.DTO;


import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * DTO used for user registration or creation.
 * Only user-entered fields are exposed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequestDTO implements Serializable {

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be 10 digits starting with 6-9")
    private String mobileNumber;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 12, max = 12, message = "Aadhaar number must be 12 digits")
    @Pattern(regexp = "\\d{12}", message = "Aadhaar number must contain only digits")
    private String adhaarNumber;


    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long, contain an uppercase letter, lowercase letter, number, and special character"
    )
    private String password;

    private MultipartFile ProfileImage; // optional

}
