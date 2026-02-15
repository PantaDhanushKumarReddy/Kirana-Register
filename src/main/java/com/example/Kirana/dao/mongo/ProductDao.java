package com.example.Kirana.dao.mongo;

import com.example.Kirana.exception.ProductNotFoundException;
import com.example.Kirana.entity.mongo.Product;
import com.example.Kirana.repository.mongo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ProductDao
 *
 * Data Access Object for Product entities.
 * Encapsulates MongoDB access and enforces active-product rules.
 */
@Component
public class ProductDao {

    private  ProductRepository repository;

    public ProductDao(ProductRepository repository) {
        this.repository = repository;
    }
    /**
     * Saves or updates a product.
     *
     * @param product Product entity to persist
     * @return Persisted Product entity
     */
    public Product save(Product product) {
        return repository.save(product);
    }
    /**
     * Retrieves an active product by ID.
     *
     * Ensures:
     *  - Only active products are returned
     *  - Inactive or deleted products are treated as not found
     *
     * @param id Product ID
     * @return Active Product
     * @throws ProductNotFoundException if no active product exists
     */
    public Product findActiveById(String id) {
        return repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
    /**
     * Retrieves all active products for a given Kirana store.
     *
     * @param kiranaId Kirana ID
     * @return List of active products
     */
    public List<Product> findByKiranaId(String kiranaId) {
        return repository.findByKiranaIdAndIsActiveTrue(kiranaId);
    }
    /**
     * Retrieves all active products for a given Kirana store and category.
     *
     * @param kiranaId      Kirana ID
     * @param category Product category
     * @return List of active products in the category
     */
    public List<Product> findByCategory(String kiranaId, String category) {
        return repository.findByKiranaIdAndCategoryAndIsActiveTrue(kiranaId, category);
    }
}
