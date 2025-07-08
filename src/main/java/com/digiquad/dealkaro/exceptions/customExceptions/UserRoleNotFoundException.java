package com.digiquad.dealkaro.exceptions.customExceptions;

public class UserRoleNotFoundException extends RuntimeException {
    public UserRoleNotFoundException(String message) {
        super(message);
    }
}
