package com.digiquad.dealkaro.exceptions;

import com.digiquad.dealkaro.exceptions.customExceptions.*;
import com.digiquad.dealkaro.exceptions.customExceptions.auth.EmptyCredentialsException;
import com.digiquad.dealkaro.exceptions.customExceptions.auth.InvalidLoginException;
import com.digiquad.dealkaro.exceptions.customExceptions.auth.InvalidRefreshTokenException;
import com.digiquad.dealkaro.model.DTO.WebResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionsHandler {

    /**
     * Generic handler for all uncaught exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponseDTO<String>> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({

            UserNotFoundException.class,
            UserRoleNotFoundException.class,
            UserNotSavedException.class,
            EmptyUserListException.class,
            ImageNotSavedException.class,
            SuperAdminNotFoundException.class,
            InvalidRefreshTokenException.class,
            UserAlreadyRegisteredException.class
    })
    public ResponseEntity<WebResponseDTO<String>> handleCommonBusinessExceptions(RuntimeException ex,
                                                                                 WebRequest request) {
        return buildResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private ResponseEntity<WebResponseDTO<String>> buildResponse(Exception ex, WebRequest request, HttpStatus status) {
        JSONObject otherInfo = new JSONObject();
        otherInfo.put("path", request.getDescription(false));
        otherInfo.put("exception", ex.getClass().getSimpleName());
        if (ex.getStackTrace().length > 0) {
            otherInfo.put("stackTrace", ex.getStackTrace()[0].toString());
        }

        WebResponseDTO<String> errorResponse = WebResponseDTO.<String>builder()
                .flag(false)
                .message(ex.getMessage())
                .status(status.value())
                .otherInfo(otherInfo)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<WebResponseDTO<String>> handleUnauthorizedExceptions(BadCredentialsException ex,
                                                                               WebRequest request) {
        return buildResponse(ex, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<WebResponseDTO<String>> handleInvalidLoginException(InvalidLoginException ex,
                                                                              WebRequest request) {
        return buildResponse(ex, request, HttpStatus.UNAUTHORIZED);
    }




    @ExceptionHandler(DeviceAlreadyRegisteredException.class)
    public ResponseEntity<WebResponseDTO<String>> handleDeviceAlreadyRegistered(DeviceAlreadyRegisteredException ex,WebRequest request) {
        return  buildResponse(ex,request,HttpStatus.CONFLICT);
    }



}