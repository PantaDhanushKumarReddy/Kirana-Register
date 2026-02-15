package com.example.Kirana.dao.mongo;

import com.example.Kirana.exception.KiranaNotFoundException;
import com.example.Kirana.entity.mongo.Kirana;
import com.example.Kirana.repository.mongo.KiranaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
/**
 * KiranaDao
 *
 * Data Access Object for Kirana entities.
 * Acts as a thin abstraction over the repository layer and
 * enforces domain-specific access rules.
 */
@Component
@RequiredArgsConstructor
public class KiranaDao {
    private final KiranaRepository kiranaRepository;
    /**
     * Saves or updates a Kirana entity.
     *
     * @param kirana Kirana entity to persist
     * @return Persisted Kirana entity
     */
    public Kirana save(Kirana kirana) {
        return kiranaRepository.save(kirana);
    }
    /**
     * Retrieves an active Kirana store by ID.
     *
     * Ensures:
     *  - Only active Kirana stores are returned
     *  - Deactivated stores are treated as not found and throws
     *   an RunTimeException such as KiranaNotFoundException
     * @param id Kirana ID
     * @return Active Kirana entity
     * @throws KiranaNotFoundException if no active Kirana exists with the given ID
     */
    public Kirana findActiveById(String id) {
        return kiranaRepository.findByIdAndIsActiveTrue(id).orElseThrow(()->new KiranaNotFoundException(id));
    }

}