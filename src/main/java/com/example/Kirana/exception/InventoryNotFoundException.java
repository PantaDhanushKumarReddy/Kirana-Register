package com.example.Kirana.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(Object id) {
        super("Inventory not found with id: " + id);
    }
}
