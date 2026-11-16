package com.sun.training.employeemanagement.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for JwtTokenProvider utility (Module 9).
 */
class JwtTokenProviderTest {

    private final String secret = "SunAsteriskTrainingEmployeeManagementSystemJwtSecretKey2026Secure";
    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(secret, 3600000);

    @Test
    @DisplayName("Module 9: Generate valid JWT token and extract subject and role")
    void shouldGenerateAndValidateToken() {
        String token = tokenProvider.generateToken("admin", "ROLE_ADMIN");

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("admin", tokenProvider.getUsernameFromToken(token));
        assertEquals("ROLE_ADMIN", tokenProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Module 9: Validate token returns false for invalid token string")
    void shouldReturnFalseForInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalid.token.string"));
    }

    @Test
    @DisplayName("Module 9: Validate token returns false for expired token")
    void shouldReturnFalseForExpiredToken() {
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(secret, -1000);
        String expiredToken = shortLivedProvider.generateToken("user", "ROLE_USER");

        assertFalse(tokenProvider.validateToken(expiredToken));
    }
}

