package com.palak.springadvanceassignment.exception;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("No todo found with id: " + id);
    }
}