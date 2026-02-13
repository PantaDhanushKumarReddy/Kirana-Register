package com.example.Kirana.repository.mongo;

import com.example.Kirana.entity.mongo.Kirana;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface KiranaRepository extends MongoRepository<Kirana, String> {

    Optional<Kirana> findByIdAndIsActiveTrue(String id);
}
