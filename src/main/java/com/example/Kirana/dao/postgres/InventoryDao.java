package com.example.Kirana.dao.postgres;

import com.example.Kirana.entity.postgres.Inventory;
import com.example.Kirana.exception.*;
import com.example.Kirana.repository.postgres.InventoryRepository;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * InventoryDao
 *
 * Data Access Object for Inventory entities stored in PostgreSQL.
 * Enforces inventory business rules such as capacity limits
 * and stock availability.
 */
@Component
public class InventoryDao {

    public InventoryDao(InventoryRepository repository) {
        this.repository = repository;
    }

    private InventoryRepository repository;
    /**
     * Creates a new inventory record.
     *
     * Business rules:
     *  - Initial quantity must not exceed capacity
     *
     * @param initialQty Initial stock quantity
     * @param capacity Maximum inventory capacity
     * @return Persisted Inventory entity
     * @throws InvalidInventoryConfigurationException if initialQty > capacity
     */
    public Inventory create(int initialQty, int capacity) {

        if (initialQty > capacity) {
            throw new InvalidInventoryConfigurationException();
        }
        Inventory inv = new Inventory();
        inv.setQuantity(initialQty);
        inv.setCapacity(capacity);
        return repository.save(inv);
    }
    /**
     * Fetch inventory by ID.
     *
     * @param id Inventory ID
     * @return Inventory entity
     * @throws InventoryNotFoundException if inventory does not exist
     */
    public Inventory findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException(id));
    }
    /**
     * Adds stock to inventory.
     *
     * Business rules:
     *  - Quantity after addition must not exceed capacity
     *
     * @param id Inventory ID
     * @param qty Quantity to add
     * @throws CapacityExceededException if capacity is exceeded
     */
    public void addStock(String id, int qty) {

        Inventory inv = findById(id);
        if (inv.getQuantity() + qty > inv.getCapacity()) {
            throw new CapacityExceededException();
        }

        inv.setQuantity(inv.getQuantity() + qty);
        repository.save(inv);
    }
    /**
    * Reduces stock from inventory.
    *
    * Business rules:
    *  - Available quantity must be sufficient
     *
     * @param id Inventory ID
     * @param qty Quantity to reduce
     * @throws InsufficientStockException if stock is insufficient
     */
    public void reduceStock(String id, int qty) {

        Inventory inv = findById(id);

        if (inv.getQuantity() < qty) {
            throw new InsufficientStockException();
        }

        inv.setQuantity(inv.getQuantity() - qty);
        repository.save(inv);
    }
    /**
     * Validates whether stock addition is possible without modifying inventory.
     *
     * Used for:
     *  - Validation during transactional workflows
     *
     * @param id Inventory ID
     * @param qty Quantity to validate
     * @throws CapacityExceededException if capacity would be exceeded
     */
    public void validateStock(String id, int qty) {
        Inventory inv = findById(id);
        if (inv.getQuantity() + qty > inv.getCapacity()) {
            throw new CapacityExceededException();
        }
    }
}
