package com.digiquad.dealkaro.exceptions.customExceptions;

public class EmptyUserListException extends RuntimeException {
    public EmptyUserListException(String message) {
        super(message);
    }
}
