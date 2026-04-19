package com.palak.springadvanceassignment.exception;

import com.palak.springadvanceassignment.enums.TodoStatus;

// Thrown when attempting an invalid todo status transition
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(TodoStatus from, TodoStatus to) {
        super("Invalid status transition from " + from + " to " + to + ". Allowed: PENDING → COMPLETED or COMPLETED → PENDING.");
    }
}