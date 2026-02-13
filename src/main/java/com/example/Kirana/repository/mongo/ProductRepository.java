package com.example.Kirana.repository.mongo;

import com.example.Kirana.entity.mongo.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByIdAndIsActiveTrue(String id);

    List<Product> findBykIdAndIsActiveTrue(String kId);

    List<Product> findBykIdAndCategoryAndIsActiveTrue(String kId, String category);
}