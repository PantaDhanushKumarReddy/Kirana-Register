package com.example.Kirana.exception;

public class InvalidInventoryConfigurationException extends RuntimeException {

    public InvalidInventoryConfigurationException() {
        super("Initial quantity cannot be greater than max quantity");
    }
}