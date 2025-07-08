package com.digiquad.dealkaro.controller;

import com.digiquad.dealkaro.model.DTO.UserDTO;
import com.digiquad.dealkaro.model.DTO.WebResponseDTO;
import com.digiquad.dealkaro.model.UserModel;
import com.digiquad.dealkaro.service.SuperAdminService;
import com.digiquad.dealkaro.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.digiquad.dealkaro.constants.EndpointConstants.*;


/**
 * REST controller responsible for managing Super Admin-specific actions
 * such as viewing and updating profile information and uploading images.
 *
 * <p>All methods are secured and accessible only by users with the SUPER_ADMIN role.</p>
 *
 *
 */
@RestController
@Slf4j
@AllArgsConstructor
public class SuperAdminController {

    private SuperAdminService superadminService;
    private UserService userService;

    /**
     * Endpoint to fetch the current Super Admin's details.
     *
     * @return {@link ResponseEntity} with {@link WebResponseDTO} containing Super Admin details
     * or HTTP 500 in case of error.
     */
    @GetMapping(SUPER_ADMIN_VIEW)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<WebResponseDTO<UserDTO>> viewSuperAdmin() {
            log.info("Getting super admin details for view purpose..");
        return ResponseEntity.ok(
                WebResponseDTO.<UserDTO>builder()
                        .flag(true)
                        .status(200)
                        .message("Super Admin details fetched successfully.")
                        .response(superadminService.getSuperAdminDetails())
                        .build()
        );

    }

    /**
     * Endpoint to update only the profile image of the logged-in Super Admin.
     *
     * @param userDetails The authenticated user's details.
     * @param file        The image file to upload.
     * @return {@link ResponseEntity} with success message or HTTP 500 in case of error.
     * @throws IOException if there is an issue saving the image file.
     */
    @PostMapping(IMAGE_UPLOAD_URL)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> updateSuperAdminProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("image") MultipartFile file) throws IOException {
        return ResponseEntity.ok(
                WebResponseDTO.<String>builder()
                        .flag(true)
                        .status(200)
                        .message("Super Admin profile image updated successfully.")
                        .response(superadminService.updateSuperAdminDetailsWithImage(userDetails.getUsername(), file))
                        .build()
        );

    }

    /**
     * Endpoint to update all Super Admin details including email, mobile number, Aadhaar, and profile image.
     *
     * @param userDetails The authenticated user's details.
     * @param file        The new profile image file.
     * @param userModel   Model containing updated user information.
     * @return {@link ResponseEntity} with result message and appropriate HTTP status.
     */
    @PutMapping(SUPER_ADMIN_UPDATE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> updateSuperAdminDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("image") MultipartFile file,
            @ModelAttribute UserModel userModel) throws IOException {
            log.info("Updating Super Admin details...");
        return ResponseEntity.ok(
                WebResponseDTO.<String>builder()
                        .flag(true)
                        .status(200)
                        .message("Super Admin details updated successfully.")
                        .response(superadminService.updateSuperAdminAllDetails(userDetails.getUsername(), file, userModel))
                        .build()
        );

    }
}
