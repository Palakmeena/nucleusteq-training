package com.palak.springcoreassignment.controller;

import com.palak.springcoreassignment.model.ApiResponse;
import com.palak.springcoreassignment.model.User;
import com.palak.springcoreassignment.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController exposes REST APIs for the User Management System.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /users
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponse.ok("All users fetched successfully.", users)
        );
    }

    // GET /users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(
                ApiResponse.ok("User fetched successfully.", user)
        );
    }

    // POST /users
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created successfully.", created));
    }
}