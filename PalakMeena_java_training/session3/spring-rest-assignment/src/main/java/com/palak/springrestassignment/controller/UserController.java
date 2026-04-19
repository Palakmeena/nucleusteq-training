package com.palak.springrestassignment.controller;

import com.palak.springrestassignment.model.ApiResponse;
import com.palak.springrestassignment.model.SubmitRequest;
import com.palak.springrestassignment.model.User;
import com.palak.springrestassignment.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// REST API endpoints for user management with search, submit, and delete operations
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Searches users with optional filters by name, age, or role
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<List<User>>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String role) {

        List<User> result = userService.searchUsers(name, age, role);
        return ResponseEntity.ok(
                ApiResponse.ok("Users fetched successfully.", result)
        );
    }

    // Creates and saves a new user from submitted request data
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<User>> submitUser(@RequestBody SubmitRequest request) {
        User saved = userService.submitUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User submitted successfully.", saved));
    }

    // Deletes a user after confirmation to prevent accidental deletion
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean confirm) {

        String message = userService.deleteUser(id, confirm);
        return ResponseEntity.ok(
                ApiResponse.ok(message, null)
        );
    }
}