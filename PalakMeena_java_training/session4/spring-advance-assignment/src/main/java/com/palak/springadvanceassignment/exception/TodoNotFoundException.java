package com.palak.springadvanceassignment.exception;

// Thrown when a requested todo with specific ID does not exist in database
public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("No todo found with id: " + id);
    }
}