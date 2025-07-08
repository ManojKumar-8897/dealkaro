package com.digiquad.dealkaro.exceptions.customExceptions.auth;

import org.springframework.security.authentication.BadCredentialsException;

public class EmptyCredentialsException extends BadCredentialsException {
    public EmptyCredentialsException(String message) {
        super(message);
    }
}
