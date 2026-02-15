package com.example.Kirana.repository.mongo;

import com.example.Kirana.entity.mongo.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
/**
 * ProductRepository
 *
 * MongoDB repository for managing Product documents.
 * Provides methods for fetching active products with store-level filtering.
 */
public interface ProductRepository extends MongoRepository<Product, String> {
    /**
     * Finds an active product by its ID.
     *
     * Used to ensure:
     *  - Inactive or discontinued products are not sold
     *
     * @param id Product ID
     * @return Optional containing active Product if found
     */
    Optional<Product> findByIdAndIsActiveTrue(String id);
    /**
     * Finds all active products for a given Kirana store.
     *
     * Used for:
     *  - Product listing screens
     *  - Inventory and catalog management
     *
     * @param kiranaId Kirana ID
     * @return List of active products for the store
     */
    List<Product> findByKiranaIdAndIsActiveTrue(String kiranaId);
    /**
            * Finds all active products for a given Kirana store and category.
            *
            * Used for:
            *  - Category-based filtering
     *  - Analytics and reporting
     *
             * @param kiranaId      Kirana ID
     * @param category Product category
     * @return List of active products matching the category
     */
    List<Product> findByKiranaIdAndCategoryAndIsActiveTrue(String kiranaId, String category);
}