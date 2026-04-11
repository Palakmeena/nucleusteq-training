package com.palak.springcoreassignment.service;

import com.palak.springcoreassignment.exception.UserNotFoundException;
import com.palak.springcoreassignment.model.User;
import com.palak.springcoreassignment.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserService handles all business logic related to users.
 *
 * Demonstrates:
 * - @Service annotation (Spring manages this as a bean)
 * - Constructor injection (no @Autowired needed)
 * - Business rules kept strictly inside the service layer
 * - Exception throwing for invalid states
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User createUser(User user) {
        validateUser(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException(
                    "A user with email '" + user.getEmail() + "' already exists."
            );
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("User name must not be empty.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("User email must not be empty.");
        }
        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("User email format is invalid.");
        }
    }
}