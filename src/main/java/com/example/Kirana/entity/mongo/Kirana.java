package com.example.Kirana.entity.mongo;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 *Kirana Entity
 *Represents a Kirana (local grocery store) record stored in MongoDB.
 */
@Data
@Document(collection = "kirana")
public class Kirana {

    @Id
    private String id; // ULID

    private String kName;
    private String location;

    private boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;
}
