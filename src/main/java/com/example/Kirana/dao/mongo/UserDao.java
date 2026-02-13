package com.example.Kirana.dao.mongo;

import com.example.Kirana.entity.mongo.User;
import com.example.Kirana.exception.UserNotFoundException;
import com.example.Kirana.repository.mongo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class UserDao {

    public UserDao(UserRepository repository) {
        this.repository = repository;
    }

    private UserRepository repository;

    public User save(User user) {
        return repository.save(user);
    }
    public User findByEmail(String email) {
        return repository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
