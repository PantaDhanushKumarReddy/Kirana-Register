package com.example.Kirana.service;

import com.example.Kirana.dao.mongo.KiranaDao;
import com.example.Kirana.dto.request.KiranaRequestDto;
import com.example.Kirana.entity.mongo.Kirana;
import org.springframework.stereotype.Service;
/**
 * KiranaService
 *
 * Handles business logic related to Kirana (store) management.
 * Acts as an intermediary between controller and DAO layers.
 */
@Service
public class KiranaService {

    private final KiranaDao kiranaDao;

    /**
     * Constructor-based dependency injection for KiranaDao.
     *
     * @param kiranaDao Kirana DAO
     */
    public KiranaService(KiranaDao kiranaDao) {
        this.kiranaDao = kiranaDao;
    }
    /**
     * Creates and registers a new Kirana store.
     *
     * Sets default values such as:
     *  - Active status
     *  - Creation timestamp
     *
     * @param dto Kirana registration request data
     * @return Persisted Kirana entity
     */
    public Kirana create(KiranaRequestDto dto) {

        Kirana kirana = new Kirana();
        kirana.setKName(dto.getKName());
        kirana.setLocation(dto.getLocation());
        kirana.setActive(true);

        return kiranaDao.save(kirana);
    }
    /**
     * Retrieves an active Kirana store by ID.
     *
     * @param id Kirana ID
     * @return Active Kirana entity
     */
    public Kirana findById(String id) {
        return kiranaDao.findActiveById(id);
    }

    /**
     * Deactivates a Kirana store (soft delete).
     *
     * Marks the store as inactive instead of permanently deleting it.
     *
     * @param id Kirana ID
     */
    public void deactivate(String id) {
        Kirana kirana = kiranaDao.findActiveById(id);
        kirana.setActive(false);
        kiranaDao.save(kirana);
    }
}
