package com.palak.springrestassignment.controller;

import com.palak.springrestassignment.model.ApiResponse;
import com.palak.springrestassignment.model.SubmitRequest;
import com.palak.springrestassignment.model.User;
import com.palak.springrestassignment.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController exposes all REST APIs for this assignment.
 *
 * Demonstrates:
 * - @RestController and @RequestMapping
 * - Constructor injection only
 * - Zero business logic in controller — pure delegation to service
 * - Proper use of @RequestParam, @RequestBody, @PathVariable
 * - Correct HTTP status codes (200, 201, 400, 404)
 *
 * APIs:
 * GET    /users/search              → Task 1: search/filter users
 * POST   /submit                    → Task 2: submit structured data
 * DELETE /users/{id}?confirm=true   → Task 3: delete with confirmation
 */
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // Task 1 — GET /users/search
    // All params optional. No params = return all. One or more = filter.
    // -------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------
    // Task 2 — POST /submit
    // Accepts JSON body. Returns 201 on success, 400 on invalid input.
    // -------------------------------------------------------------------------
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<User>> submitUser(@RequestBody SubmitRequest request) {
        User saved = userService.submitUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User submitted successfully.", saved));
    }

    // -------------------------------------------------------------------------
    // Task 3 — DELETE /users/{id}?confirm=true
    // confirm=false or missing → do NOT delete, return message.
    // confirm=true → delete user.
    // -------------------------------------------------------------------------
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