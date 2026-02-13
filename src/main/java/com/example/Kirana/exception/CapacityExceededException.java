package com.example.Kirana.exception;

public class CapacityExceededException extends RuntimeException {
    public CapacityExceededException() {
        super("Inventory capacity exceeded");
    }
}

