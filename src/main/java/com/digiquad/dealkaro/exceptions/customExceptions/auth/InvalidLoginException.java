package com.digiquad.dealkaro.exceptions.customExceptions.auth;

public class InvalidLoginException extends RuntimeException{
    public InvalidLoginException(String message) {
        super(message);
    }
}
