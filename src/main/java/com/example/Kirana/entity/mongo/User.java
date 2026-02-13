package com.example.Kirana.entity.mongo;

import com.example.Kirana.enums.Role;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
/**
 * User Entity
 * Used for authentication, authorization, and role-based access control
 * within the Kirana system.
 */
@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String kId; //Kirana Store Id

    private String email;

    private String password;

    private Role role;

    private boolean isActive;

    private Instant createdAt;
}
