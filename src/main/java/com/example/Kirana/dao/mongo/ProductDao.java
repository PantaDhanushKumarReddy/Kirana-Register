package com.example.Kirana.dao.mongo;

import com.example.Kirana.exception.ProductNotFoundException;
import com.example.Kirana.entity.mongo.Product;
import com.example.Kirana.repository.mongo.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * ProductDao
 *
 * Data Access Object for Product entities.
 * Encapsulates MongoDB access and enforces active-product rules.
 */
@Component
public class ProductDao {
    private static final String PRODUCT_KEY = "product:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    //Jackson JSON serializer/deserializer.
    private final ObjectMapper objectMapper = new ObjectMapper();
    private  ProductRepository repository;
    private StringRedisTemplate redisTemplate;
    public ProductDao(ProductRepository repository,StringRedisTemplate redisTemplate) {

        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }
    /**
     * Saves or updates a product.
     *
     * @param product Product entity to persist
     * @return Persisted Product entity
     */
    public Product save(Product product) {
        Product saved = repository.save(product);
        //invalidate redis cache
        redisTemplate.delete("product:" + saved.getId());
        return saved;
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
        // Read through cache impl -> getFromCache() ? return : getFromDB && setInCache() && return
        String redisKey = PRODUCT_KEY + id;
        String cachedJson = redisTemplate.opsForValue().get(redisKey);
        if (cachedJson != null) {
            try {
                Product cached = objectMapper.readValue(
                        cachedJson, Product.class);
                return cached;
            } catch (Exception ignored) {
                // corrupted cache → fallback to DB
            }
        }
        Product product=repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        try {
            redisTemplate.opsForValue().set(
                    redisKey,
                    objectMapper.writeValueAsString(product),
                    CACHE_TTL
            );
        } catch (Exception ignored) {}

        return product;
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
