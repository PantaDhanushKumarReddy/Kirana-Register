package com.example.Kirana.repository.postgres;

import com.example.Kirana.entity.postgres.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * InventoryRepository
 *
 * JPA repository for managing Inventory entities stored in PostgreSQL.
 * Used to track stock quantity and capacity for products.
 */
public interface InventoryRepository extends JpaRepository<Inventory, String> {
}
