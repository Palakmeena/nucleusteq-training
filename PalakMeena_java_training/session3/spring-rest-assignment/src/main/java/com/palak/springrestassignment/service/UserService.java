package com.palak.springrestassignment.service;

import com.palak.springrestassignment.exception.UserNotFoundException;
import com.palak.springrestassignment.model.SubmitRequest;
import com.palak.springrestassignment.model.User;
import com.palak.springrestassignment.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService contains ALL business logic for this application.
 *
 * Demonstrates:
 * - @Service annotation
 * - Constructor injection
 * - Filtering logic using Java Streams
 * - Manual input validation
 * - Delete with confirmation check
 * - All business rules strictly inside service — zero logic in controller
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Task 1 — Search/filter users.
     * If all params are null → return all users.
     * If one or more params provided → filter by those params (AND condition).
     * Name and role: case-insensitive. Age: exact match.
     */
    public List<User> searchUsers(String name, Integer age, String role) {
        List<User> users = userRepository.findAll();

        // If no filter params passed at all, return everything
        if (name == null && age == null && role == null) {
            return users;
        }

        return users.stream()
                .filter(user -> name == null || user.getName().equalsIgnoreCase(name))
                .filter(user -> age  == null || user.getAge().equals(age))
                .filter(user -> role == null || user.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    /**
     * Task 2 — Accept and validate structured data submission.
     * Validates: name, email, role must not be null/empty.
     * Age must be positive.
     * Returns saved user on success, throws IllegalArgumentException on failure.
     */
    public User submitUser(SubmitRequest request) {
        validateSubmitRequest(request);

        User user = new User();
        user.setName(request.getName().trim());
        user.setAge(request.getAge());
        user.setRole(request.getRole() == null || request.getRole().isBlank()
                ? "USER"
                : request.getRole().trim().toUpperCase());
        user.setEmail(request.getEmail().trim());

        return userRepository.save(user);
    }

    /**
     * Task 3 — Delete user only when confirm=true.
     * If confirm is false or not passed → return confirmation message, do NOT delete.
     * If confirm=true → verify user exists, then delete.
     */
    public String deleteUser(Long id, boolean confirm) {
        if (!confirm) {
            return "Confirmation required. Please send confirm=true to proceed with deletion.";
        }

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        return "User with id " + id + " has been deleted successfully.";
    }

    // -------------------------------------------------------------------------
    // Private validation helper — business logic stays in service
    // -------------------------------------------------------------------------

    private void validateSubmitRequest(SubmitRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body must not be null.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name must not be empty.");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email must not be empty.");
        }
        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email format is invalid.");
        }
        if (request.getAge() == null) {
            throw new IllegalArgumentException("Age must not be null.");
        }
        if (request.getAge() <= 0) {
            throw new IllegalArgumentException("Age must be a positive number.");
        }
    }
}