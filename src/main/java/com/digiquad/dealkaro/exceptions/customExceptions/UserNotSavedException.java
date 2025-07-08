package com.digiquad.dealkaro.exceptions.customExceptions;

public class UserNotSavedException extends RuntimeException {
    public UserNotSavedException(String message) {
        super(message);
    }
}
