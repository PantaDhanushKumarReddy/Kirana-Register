package com.example.Kirana.dao.postgres;

import com.example.Kirana.entity.postgres.Inventory;
import com.example.Kirana.exception.*;
import com.example.Kirana.repository.postgres.InventoryRepository;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Transactional
public class InventoryDao {

    public InventoryDao(InventoryRepository repository) {
        this.repository = repository;
    }

    private InventoryRepository repository;

    public Inventory create(int initialQty, int capacity) {

        if (initialQty > capacity) {
            throw new InvalidInventoryConfigurationException();
        }
        Inventory inv = new Inventory();
        inv.setId(UlidCreator.getUlid().toString());
        inv.setQuantity(initialQty);
        inv.setCapacity(capacity);
        inv.setUpdatedAt(Instant.now());

        return repository.save(inv);
    }

    public Inventory findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException(id));
    }

    public void addStock(String id, int qty) {

        Inventory inv = findById(id);
        if (inv.getQuantity() + qty > inv.getCapacity()) {
            throw new CapacityExceededException();
        }

        inv.setQuantity(inv.getQuantity() + qty);
        inv.setUpdatedAt(Instant.now());
        repository.save(inv);
    }

    public void reduceStock(String id, int qty) {

        Inventory inv = findById(id);

        if (inv.getQuantity() < qty) {
            throw new InsufficientStockException();
        }

        inv.setQuantity(inv.getQuantity() - qty);
        inv.setUpdatedAt(Instant.now());
        repository.save(inv);
    }
    public void validateStock(String id, int qty) {
        Inventory inv = findById(id);
        if (inv.getQuantity() + qty > inv.getCapacity()) {
            throw new CapacityExceededException();
        }
    }
}
