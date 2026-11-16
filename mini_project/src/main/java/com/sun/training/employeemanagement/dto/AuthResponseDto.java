package com.sun.training.employeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object returned upon successful authentication containing JWT token (Module 9).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String role;

    /**
     * Convenient constructor.
     *
     * @param token JWT token string
     * @param username authenticated username
     * @param role assigned role name
     */
    public AuthResponseDto(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
}

