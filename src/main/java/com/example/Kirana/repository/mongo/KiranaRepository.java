package com.example.Kirana.repository.mongo;

import com.example.Kirana.entity.mongo.Kirana;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
/**
 * KiranaRepository
 *
 * MongoDB repository for managing Kirana (store) documents.
 */
public interface KiranaRepository extends MongoRepository<Kirana, String> {
    /**
     * Finds an active Kirana store by its ID.
     *
     * Used to ensure:
     *  - Deactivated stores are not accessible
     *  - Only active Kirana records are processed in business logic
     *
     * @param id Kirana ID
     * @return Optional containing active Kirana if found
     */

    Optional<Kirana> findByIdAndIsActiveTrue(String id);
}
