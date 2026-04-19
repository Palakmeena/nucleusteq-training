package com.palak.springrestassignment.exception;

// Thrown when a requested user is not found in the repository
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("No user found with id: " + id);
    }
}