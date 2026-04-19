package com.palak.springrestassignment.repository;

import com.palak.springrestassignment.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
// Data access layer providing in-memory user storage and retrieval operations
public class UserRepository {

    private final List<User> store = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public UserRepository() {
        store.add(new User(idCounter.getAndIncrement(), "Priya Sharma",  25, "USER",      "priya@example.com"));
        store.add(new User(idCounter.getAndIncrement(), "Aryan Singh",   30, "USER",      "aryan@example.com"));
        store.add(new User(idCounter.getAndIncrement(), "Sneha Patel",   28, "MODERATOR", "sneha@example.com"));
        store.add(new User(idCounter.getAndIncrement(), "Rahul Verma",   30, "ADMIN",     "rahul@example.com"));
        store.add(new User(idCounter.getAndIncrement(), "Palak Joshi",   22, "USER",      "palak@example.com"));
        store.add(new User(idCounter.getAndIncrement(), "Neha Gupta",    35, "MODERATOR", "neha@example.com"));
        store.add(new User(idCounter.getAndIncrement(), "Vikram Rao",    30, "USER",      "vikram@example.com"));
    }

    // Returns a copy of all users in the in-memory store
    public List<User> findAll() {
        return new ArrayList<>(store);
    }

    // Finds a user by ID and returns it wrapped in Optional
    public Optional<User> findById(Long id) {
        return store.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    // Checks if a user with given ID exists in the store
    public boolean existsById(Long id) {
        return store.stream().anyMatch(user -> user.getId().equals(id));
    }

    // Assigns ID and adds new user to the store
    public User save(User user) {
        user.setId(idCounter.getAndIncrement());
        store.add(user);
        return user;
    }

    // Removes user with matching ID from the store
    public void deleteById(Long id) {
        store.removeIf(user -> user.getId().equals(id));
    }
}