package com.palak.springadvanceassignment.dto;

import com.palak.springadvanceassignment.enums.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;


public class TodoDTO {

    // Data transfer object for todo API requests/responses with validation constraints
    private Long id;

    @NotBlank(message = "Title must not be blank.")
    @Size(min = 3, message = "Title must be at least 3 characters long.")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    private TodoStatus status;

    private LocalDateTime createdAt;

    public TodoDTO() {}

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TodoStatus getStatus() { return status; }
    public void setStatus(TodoStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}