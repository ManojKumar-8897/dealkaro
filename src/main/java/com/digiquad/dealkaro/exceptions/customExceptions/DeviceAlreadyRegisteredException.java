package com.digiquad.dealkaro.exceptions.customExceptions;

public class DeviceAlreadyRegisteredException extends RuntimeException {
    public DeviceAlreadyRegisteredException(String message) {
        super(message);
    }
}