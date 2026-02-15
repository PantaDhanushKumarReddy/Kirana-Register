package com.example.Kirana.service;

import com.example.Kirana.dao.postgres.InventoryDao;
import com.example.Kirana.dao.mongo.ProductDao;
import com.example.Kirana.dto.request.ProductRequestDto;
import com.example.Kirana.entity.mongo.Product;
import com.example.Kirana.entity.postgres.Inventory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
/**
 * ProductService
 *
 * Handles business logic related to products and inventory.
 * Coordinates between MongoDB (Product) and PostgreSQL (Inventory).
 */
@Service
public class ProductService {

    private final ProductDao productDao;
    private final KiranaService kiranaService;
    private final InventoryDao inventoryDao;
    /**
     * Constructor-based dependency injection.
     *
     * @param productDao Product DAO
     * @param kiranaService Kirana service
     * @param inventoryDao Inventory DAO
     */
    @Autowired(required = false)
    public ProductService(ProductDao productDao, KiranaService kiranaService, InventoryDao inventoryDao) {
        this.productDao = productDao;
        this.kiranaService = kiranaService;
        this.inventoryDao = inventoryDao;
    }
    /**
     * Adds multiple products for a given Kirana store.
     *
     * Flow:
     *  1. Validate Kirana existence
     *  2. Create inventory record in PostgreSQL
     *  3. Create product record in MongoDB
     *  4. Link product with inventory ID
     *
     * @param kiranaId Kirana ID
     * @param dto List of product creation requests
     * @return List of created products
     */
    public List<Product> addProducts(String kiranaId,List<ProductRequestDto> dto){
        kiranaService.findById(kiranaId);
        List<Product> products = new ArrayList<>();
        for (ProductRequestDto productRequestDto : dto) {
            Product product = new Product();
            Inventory inventory = inventoryDao.create(productRequestDto.getInitialQuantity(),productRequestDto.getCapacity());
            product.setKiranaId(kiranaId);
            product.setInventoryId(inventory.getId());
            product.setDescription(productRequestDto.getDescription());
            product.setProName(productRequestDto.getProName());
            product.setCategory(productRequestDto.getCategory());
            product.setCurrency("INR");
            product.setActive(true);
            product.setUnitPrice(productRequestDto.getUnitPrice());
            productDao.save(product);
            products.add(product);
        }
        return products;
    }
    /**
     * Fetch an active product by ID.
     *
     * @param id Product ID
     * @return Active product
     */
    public Product getById(String id) {

        return productDao.findActiveById(id);
    }
    /**
     * Fetch all active products for a Kirana store.
     *
     * @param kiranaId Kirana ID
     * @return List of active products
     */
    public List<Product> getAll(String kiranaId) {
        return productDao.findByKiranaId(kiranaId);
    }
    /**
     * Fetch all active products for a Kirana store filtered by category.
     *
     * @param kiranId Kirana ID
     * @param category Product category
     * @return List of matching products
     */
    public List<Product> getByCategory(String kiranId,String category) {
        return productDao.findByCategory(kiranId, category);
    }
    /**
     * Increase stock quantity for a product.
     *
     * @param productId Product ID
     * @param quantity Quantity to add
     */
    @Transactional
    public void addStock(String productId, int quantity) {

        Product product = productDao.findActiveById(productId);

        inventoryDao.addStock(
                product.getInventoryId(),
                quantity
        );
    }
    /**
     * Reduce stock quantity for a product.
     *
     * @param productId Product ID
     * @param quantity Quantity to reduce
     */
    @Transactional
    public void reduceStock(String productId, int quantity) {

        Product product = productDao.findActiveById(productId);

        inventoryDao.reduceStock(
                product.getInventoryId(),
                quantity
        );
    }
    /**
     * Deactivates a product (soft delete).
     *
     * @param productId Product ID
     */
    public void deactivate(String productId) {

        Product product = productDao.findActiveById(productId);
        product.setActive(false);
        productDao.save(product);
    }
}
