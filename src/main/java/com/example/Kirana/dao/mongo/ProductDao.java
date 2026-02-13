package com.example.Kirana.dao.mongo;

import com.example.Kirana.exception.ProductNotFoundException;
import com.example.Kirana.entity.mongo.Product;
import com.example.Kirana.repository.mongo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductDao {

    private  ProductRepository repository;

    public ProductDao(ProductRepository repository) {
        this.repository = repository;
    }

    public Product save(Product product) {
        return repository.save(product);
    }

    public Product findActiveById(String id) {
        return repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> findBykId(String kId) {
        return repository.findBykIdAndIsActiveTrue(kId);
    }

    public List<Product> findByCategory(String kId, String category) {
        return repository.findBykIdAndCategoryAndIsActiveTrue(kId, category);
    }
}
