package com.palak.springrestassignment.model;

// Request DTO for accepting user submissions with validation constraints
public class SubmitRequest {

    private String name;
    private Integer age;
    private String role;
    private String email;

    public SubmitRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}