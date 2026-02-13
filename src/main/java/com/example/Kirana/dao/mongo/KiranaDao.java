package com.example.Kirana.dao.mongo;

import com.example.Kirana.exception.KiranaNotFoundException;
import com.example.Kirana.entity.mongo.Kirana;
import com.example.Kirana.repository.mongo.KiranaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KiranaDao {
    private final KiranaRepository kiranaRepository;
    public Kirana save(Kirana kirana) {
        return kiranaRepository.save(kirana);
    }
    public Kirana findActiveById(String id) {
        return kiranaRepository.findByIdAndIsActiveTrue(id).orElseThrow(()->new KiranaNotFoundException(id));
    }

}