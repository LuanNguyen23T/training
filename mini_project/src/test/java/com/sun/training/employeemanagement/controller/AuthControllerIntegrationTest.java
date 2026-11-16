package com.sun.training.employeemanagement.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.training.employeemanagement.dto.LoginRequestDto;
import com.sun.training.employeemanagement.dto.RegisterRequestDto;
import com.sun.training.employeemanagement.entity.Role;
import com.sun.training.employeemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for AuthController registration, login, and profile endpoints (Module 9).
 */
@SpringBootTest
@ActiveProfiles("dev")
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Module 9: Register new user successfully with JWT returned")
    void shouldRegisterNewUserSuccessfully() throws Exception {
        String uniqueUsername = "testuser_" + System.currentTimeMillis();
        RegisterRequestDto request = new RegisterRequestDto(uniqueUsername, "password123", Role.ROLE_USER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(uniqueUsername))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        // Cleanup: remove created test user immediately so DB stays clean
        userRepository.findByUsername(uniqueUsername)
                .ifPresent(userRepository::delete);
    }

    @Test
    @DisplayName("Module 9: Login with valid credentials returns 200 and JWT token")
    void shouldLoginSuccessfullyWithValidCredentials() throws Exception {
        LoginRequestDto request = new LoginRequestDto("admin", "admin123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Module 9: Login with invalid password returns 401 Unauthorized")
    void shouldFailLoginWithInvalidPassword() throws Exception {
        LoginRequestDto request = new LoginRequestDto("admin", "wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Module 9: Get current user profile using JWT Bearer token")
    void shouldGetCurrentUserProfileWithJwtToken() throws Exception {
        // First login to acquire token
        LoginRequestDto loginRequest = new LoginRequestDto("user", "user123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();

        // Call /me endpoint with Bearer token
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.authorities").value("ROLE_USER"));
    }
}
