package com.example.Kirana.exception;

public class KiranaNotFoundException extends RuntimeException {

    public KiranaNotFoundException(Object id) {
        super("Kirana not found with id: " + id);
    }
}
