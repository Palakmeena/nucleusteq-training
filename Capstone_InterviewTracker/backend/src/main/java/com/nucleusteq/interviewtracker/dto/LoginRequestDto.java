package com.nucleusteq.interviewtracker.dto;

import com.nucleusteq.interviewtracker.util.AppConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDto {

    @NotBlank(message = AppConstants.EMAIL_REQUIRED)
    @Email(message = AppConstants.INVALID_EMAIL)
    private String email;

    @NotBlank(message = AppConstants.PASSWORD_REQUIRED)
    private String password;

    public LoginRequestDto() {}

    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}