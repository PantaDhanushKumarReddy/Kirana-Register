package com.example.Kirana.service;

import com.example.Kirana.dao.postgres.InventoryDao;
import com.example.Kirana.dao.mongo.ProductDao;
import com.example.Kirana.dto.request.ProductRequestDto;
import com.example.Kirana.entity.mongo.Product;
import com.example.Kirana.entity.postgres.Inventory;
import com.github.f4b6a3.ulid.UlidCreator;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductDao productDao;
    private final KiranaService kiranaService;
    private final InventoryDao inventoryDao;

    @Autowired(required = false)
    public ProductService(ProductDao productDao, KiranaService kiranaService, InventoryDao inventoryDao) {
        this.productDao = productDao;
        this.kiranaService = kiranaService;
        this.inventoryDao = inventoryDao;
    }

    private String getkId() {
        Claims claims = (Claims) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return claims.get("kId", String.class);
    }

    public List<Product> addProducts(List<ProductRequestDto> dto){
        String kid=getkId();
        kiranaService.findById(kid);
        List<Product> products = new ArrayList<>();
        for (ProductRequestDto productRequestDto : dto) {
            Product product = new Product();
            Inventory inventory = inventoryDao.create(productRequestDto.getInitialQuantity(),productRequestDto.getCapacity());
            product.setId(UlidCreator.getUlid().toString());
            product.setKId(kid);
            product.setInventoryId(inventory.getId());
            product.setProName(productRequestDto.getProName());
            product.setCategory(productRequestDto.getCategory());
            product.setCurrency("INR");
            product.setActive(true);
            product.setCreatedAt(Instant.now());
            product.setUpdatedAt(Instant.now());
            product.setUnitPrice(productRequestDto.getUnitPrice());
            productDao.save(product);
            products.add(product);
        }
        return products;
    }


    public Product getById(String id) {
        return productDao.findActiveById(id);
    }
    public List<Product> getAll() {
        String kId=getkId();
        return productDao.findBykId(kId);
    }
    public List<Product> getByCategory(String category) {
        String kId=getkId();
        return productDao.findByCategory(kId, category);
    }
    public void delete(String id) {
        Product product = productDao.findActiveById(id);
        product.setActive(false);
        productDao.save(product);
    }

    public void addStock(String productId, int quantity) {

        Product product = productDao.findActiveById(productId);

        inventoryDao.addStock(
                product.getInventoryId(),
                quantity
        );
    }

    public void reduceStock(String productId, int quantity) {

        Product product = productDao.findActiveById(productId);

        inventoryDao.reduceStock(
                product.getInventoryId(),
                quantity
        );
    }
    public void deactivate(String productId) {

        Product product = productDao.findActiveById(productId);

        product.setActive(false);
        product.setUpdatedAt(Instant.now());

        productDao.save(product);
    }
}
