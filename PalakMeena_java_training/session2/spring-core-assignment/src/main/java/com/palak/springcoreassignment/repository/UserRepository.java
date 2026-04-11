package com.palak.springcoreassignment.repository;

import com.palak.springcoreassignment.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {

    private final List<User> store = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public UserRepository() {
        store.add(new User(idCounter.getAndIncrement(), "Palak Sharma",  "palak@example.com",  "ADMIN"));
        store.add(new User(idCounter.getAndIncrement(), "Riya Joshi",    "riya@example.com",   "USER"));
        store.add(new User(idCounter.getAndIncrement(), "Aryan Singh",   "aryan@example.com",  "USER"));
        store.add(new User(idCounter.getAndIncrement(), "Sneha Patel",   "sneha@example.com",  "MODERATOR"));
    }

    public List<User> findAll() {
        return new ArrayList<>(store);
    }

    public Optional<User> findById(Long id) {
        return store.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        return store.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public User save(User user) {
        user.setId(idCounter.getAndIncrement());
        store.add(user);
        return user;
    }
}