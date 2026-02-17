package com.example.Kirana.dao.mongo;

import com.example.Kirana.entity.mongo.User;
import com.example.Kirana.exception.UserNotFoundException;
import com.example.Kirana.repository.mongo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class UserDao {


    private UserRepository repository;
    /**
     * Constructor-based dependency injection.
     *
     * @param repository User repository
     */
    public UserDao(UserRepository repository) {
        this.repository = repository;
    }
    /**
     * Saves a user document.
     *
     * @param user User entity to be saved
     * @return Persisted user entity
     */
    public User save(User user) {
        return repository.save(user);
    }
    /**
     * Finds an active user by email address.
     *
     * @param email User email
     * @return Active user
     * @throws UserNotFoundException if no active user exists with the given email
     */
    public User findByEmail(String email) {
        return repository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
    public User findById(String id){
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
