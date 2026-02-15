package com.example.Kirana.entity.mongo;

import com.example.Kirana.enums.Role;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;

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
    private String kiranaId; //Kirana Store Id

    private String email;

    private String password;

    private Role role;

    private boolean isActive;
    @CreatedDate
    private Date createdAt;
}
