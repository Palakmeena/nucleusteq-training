package com.nucleusteq.interviewtracker.mapper;

import com.nucleusteq.interviewtracker.dto.LoginResponseDto;
import com.nucleusteq.interviewtracker.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper for authentication related conversions.
 */
@Component
public class AuthMapper {

    /**
     * Maps User entity and token to LoginResponseDto.
     *
     * @param user  the authenticated user
     * @param token the generated JWT token
     * @return the mapped login response DTO
     */
    public LoginResponseDto mapToLoginResponse(User user, String token) {
        return new LoginResponseDto(
                token,
                user.getRole().name(),
                user.getFullName(),
                user.getEmail()
        );
    }
}
