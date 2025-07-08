package com.digiquad.dealkaro.controller;


import com.digiquad.dealkaro.model.AdminModel;
import com.digiquad.dealkaro.model.DTO.AdminDTO;
import com.digiquad.dealkaro.model.DTO.WebResponseDTO;
import com.digiquad.dealkaro.service.AdminService;
import com.digiquad.dealkaro.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.digiquad.dealkaro.constants.EndpointConstants.*;


@RestController
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @Autowired
    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @PostMapping(ADMIN_API)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> createAdmin(@RequestBody AdminModel adminDetails) {
        try {
            log.info("Creating new Admin..");
            adminService.createAdmin(adminDetails);
            return ResponseEntity.ok(
                    WebResponseDTO.<String>builder()
                            .flag(true)
                            .status(HttpStatus.OK.value())
                            .message("Admin created successfully.")
                            .build()
            );

        } catch (Exception e) {
            log.error("Error in creating new Admin : ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(APPROVE_USER)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> approveUser(@PathVariable String userId,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("Approving registered users..");
            userService.approveUser(userId, userDetails.getUsername());
            return ResponseEntity.ok(
                    WebResponseDTO.<String>builder()
                            .flag(true)
                            .status(HttpStatus.OK.value())
                            .message("User approved successfully.")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error in approving user from admin side : ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(ADMIN_VIEW)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<WebResponseDTO<AdminDTO>> viewAdmin(@RequestParam String adminId) {
        try {
            log.info("Getting admin details for view purpose..");
            return ResponseEntity.ok(
                    WebResponseDTO.<AdminDTO>builder()
                            .flag(true)
                            .status(HttpStatus.OK.value())
                            .message("Admin details fetched successfully.")
                            .response(adminService.getAdminDetails(adminId))
                            .build()
            );

        } catch (Exception e) {
            log.error("Error in fetching Admin details : ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @PutMapping(ADMIN_UPDATE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> updateAdminDetails(@RequestBody AdminModel adminDetails) {
        try {
            log.info("Updating Admin details..");
            adminService.updateAdminDetails(adminDetails);
            return ResponseEntity.ok(
                    WebResponseDTO.<String>builder()
                            .flag(true)
                            .status(HttpStatus.OK.value())
                            .message("Admin details updated successfully.")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error in updating user from admin side : ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(ADMIN_DELETE)
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> removeAdminDetails(@PathVariable String id,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("Deleting or Hibernating admin from system..");
            adminService.deleteAdminDetails(id, userDetails.getUsername());
            return ResponseEntity.ok(
                    WebResponseDTO.<String>builder()
                            .flag(true)
                            .status(HttpStatus.OK.value())
                            .message("Admin deleted successfully.")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error in deleting or hibernating admin details : ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
