package com.example.Kirana.repository.mongo;

import com.example.Kirana.entity.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * UserRepository
 *
 * MongoDB repository for managing User documents.
 * Used primarily for authentication and user management.
 */
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Finds an active user by email address.
     *
     * Used during:
     *  - User login
     *  - Token validation
     *  - Access control checks
     *
     * Ensures inactive or disabled users cannot authenticate.
     *
     * @param email User email address
     * @return Optional containing active User if found
     */
    Optional<User> findByEmailAndIsActiveTrue(String email);
}
