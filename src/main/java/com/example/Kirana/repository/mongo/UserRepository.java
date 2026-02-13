package com.example.Kirana.repository.mongo;

import com.example.Kirana.entity.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmailAndIsActiveTrue(String email);
}
