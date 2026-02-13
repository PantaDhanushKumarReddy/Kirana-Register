package com.example.Kirana.repository.postgres;

import com.example.Kirana.entity.postgres.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
}
