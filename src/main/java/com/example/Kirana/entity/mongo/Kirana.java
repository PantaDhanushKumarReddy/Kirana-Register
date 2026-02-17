package com.example.Kirana.entity.mongo;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;

/**
 *Kirana Entity
 *Represents a Kirana (local grocery store) record stored in MongoDB.
 */
@Data
@Document(collection = "kirana")
public class Kirana {

    @Id
    private String id; // MongoDB Object Id

    private String kName;
    private String location;

    private boolean isActive;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
