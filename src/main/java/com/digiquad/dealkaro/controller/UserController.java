package com.digiquad.dealkaro.controller;


import com.digiquad.dealkaro.constants.EnumConstants;
import com.digiquad.dealkaro.entity.User;
import com.digiquad.dealkaro.model.DTO.*;
import com.digiquad.dealkaro.model.UserModel;
import com.digiquad.dealkaro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.digiquad.dealkaro.constants.EndpointConstants.*;


@RestController
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(USER_REGISTER)
    public ResponseEntity<WebResponseDTO<?>> registerUser(
            @Valid @RequestBody UserRegisterRequestDTO user,
            BindingResult result) {

        // Check for validation errors
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage(),
                            (msg1, msg2) -> msg1 // handles duplicate field errors
                    ));

            return ResponseEntity.badRequest().body(
                    WebResponseDTO.builder()
                            .flag(false)
                            .message("Validation failed")
                            .status(HttpStatus.BAD_REQUEST.value())
                            .response(errors)
                            .build()
            );

        }

        // Proceed with user registration
        userService.registerUser(user);

        return ResponseEntity.ok(
                WebResponseDTO.builder()
                        .flag(true)
                        .message("User registered successfully")
                        .status(HttpStatus.CREATED.value())
                        .build()
        );

    }


    @GetMapping(USER_API)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<WebResponseDTO<List<UserDTO>>> getUsersList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) EnumConstants.Role userRole) {

        log.info("Fetching user List...");

        List<UserDTO> users = userService.getAllUsersList(userDetails.getUsername(), pageNumber, pageSize, searchValue, userRole);

        return ResponseEntity.ok(
                WebResponseDTO.<List<UserDTO>>builder()
                        .flag(true)
                        .status(HttpStatus.OK.value())
                        .message("User list fetched successfully")
                        .response(users)
                        .build()
        );
    }


    @GetMapping(USER_VIEW)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER')")
    public ResponseEntity<WebResponseDTO<UserDTO>> viewUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Fetching user details...");
        UserDTO userDto = userService.getUserDetails(userDetails.getUsername());
        return ResponseEntity.ok(WebResponseDTO.<UserDTO>builder()
                .flag(true)
                .message("User details fetched successfully.")
                .status(HttpStatus.OK.value())
                .response(userDto)
                .build());

    }


    @PutMapping(USER_UPDATE)
    @Operation(summary = "Update product with image upload",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = UserModel.class))))
    public ResponseEntity<WebResponseDTO<String>> updateUser(@ModelAttribute UserModel user,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Updating user details...");
        userService.updateUser(user, userDetails.getUsername());
        return ResponseEntity.ok(WebResponseDTO.<String>builder()
                .flag(true)
                .message("User updated successfully.")
                .status(HttpStatus.OK.value())
                .build());

    }

    @DeleteMapping(USER_DELETE)
    @PreAuthorize("hasRole('USER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> deleteUser(@PathVariable String id,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Deleting user...");
        userService.deleteUser(id, userDetails.getUsername());
        return ResponseEntity.ok(WebResponseDTO.<String>builder()
                .flag(true)
                .message("User deleted successfully.")
                .status(HttpStatus.OK.value())
                .build());

    }



    @GetMapping(USER_SET_PASSWORD)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WebResponseDTO<String>> setPassword(@ModelAttribute SetPasswordRequest request,@AuthenticationPrincipal UserDetails userDetails) {

        userService.setPassword( request.getNewPassword(),userDetails.getUsername());

        return ResponseEntity.ok(WebResponseDTO.<String>builder()
                .flag(true)
                .message("Password set successfully.")
                .status(HttpStatus.OK.value())
                .build());

    }


    @PostMapping(USER_LOGOUT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER')")
    public ResponseEntity<LogoutResponseDTO> logoutCurrentDevice(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @RequestBody LogoutRequestDTO dto) {
        return ResponseEntity.ok(userService.logoutFromCurrentDevice(userDetails, dto));
    }

    @PostMapping(USER_LOGOUT_ALL)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER')")
    public ResponseEntity<LogoutResponseDTO> logoutAllDevices(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.logoutFromAllDevices(userDetails));
    }

    @GetMapping(USER_SESSIONS)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER')")
    public ResponseEntity<MySessionsResponseDTO> mySessions(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMySessions(userDetails));
    }







}
