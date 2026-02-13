package com.example.Kirana.service;

import com.example.Kirana.dao.mongo.KiranaDao;
import com.example.Kirana.dto.request.KiranaRequestDto;
import com.example.Kirana.entity.mongo.Kirana;
import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class KiranaService {

    private final KiranaDao kiranaDao;

    public Kirana create(KiranaRequestDto dto) {

        Kirana kirana = new Kirana();
        kirana.setId(UlidCreator.getUlid().toString());
        kirana.setKName(dto.getKName());
        kirana.setLocation(dto.getLocation());
        kirana.setActive(true);
        kirana.setCreatedAt(Instant.now());
        kirana.setUpdatedAt(Instant.now());

        return kiranaDao.save(kirana);
    }

    public Kirana findById(String id) {
        return kiranaDao.findActiveById(id);
    }
    public void deactivate(String id) {

        Kirana kirana = kiranaDao.findActiveById(id);

        kirana.setActive(false);
        kirana.setUpdatedAt(Instant.now());

        kiranaDao.save(kirana);
    }
}
