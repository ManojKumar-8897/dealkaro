package com.digiquad.dealkaro.exceptions.customExceptions;

import java.io.IOException;

public class ImageNotSavedException extends IOException {
    public ImageNotSavedException(String message) {
        super(message);
    }
}
