package com.sun.training.employeemanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.sun.training.employeemanagement.dto.RegisterRequestDto;
import com.sun.training.employeemanagement.exception.DuplicateResourceException;
import com.sun.training.employeemanagement.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthWebControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthWebController authWebController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authWebController).build();
    }

    @Test
    @DisplayName("GET / should redirect to /employees/list")
    void testRootRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees/list"));
    }

    @Test
    @DisplayName("GET /login should display login view")
    void testShowLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("GET /login with params should display appropriate flash messages")
    void testShowLoginPageWithParams() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("errorMessage"));

        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("successMessage"));

        mockMvc.perform(get("/login").param("registered", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("GET /register should display registration view")
    void testShowRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("registerDto"));
    }

    @Test
    @DisplayName("GET /access-denied should display 403 view")
    void testShowAccessDeniedPage() throws Exception {
        mockMvc.perform(get("/access-denied"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/403"));
    }

    @Test
    @DisplayName("GET /check-username should return exists flag")
    void testCheckUsername() throws Exception {
        Mockito.when(authService.isUsernameExists("existing_user")).thenReturn(true);
        Mockito.when(authService.isUsernameExists("new_user")).thenReturn(false);

        mockMvc.perform(get("/check-username").param("username", "existing_user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        mockMvc.perform(get("/check-username").param("username", "new_user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    @DisplayName("POST /register with valid credentials should persist user and redirect to login")
    void testProcessRegistrationSuccess() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "newstaff")
                        .param("password", "staff123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));

        verify(authService).register(any(RegisterRequestDto.class));
    }

    @Test
    @DisplayName("POST /register with invalid input should return register view with errors")
    void testProcessRegistrationValidationFailure() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "a")
                        .param("password", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerDto", "username", "password"));
    }

    @Test
    @DisplayName("POST /register with duplicate username should return register view with error message")
    void testProcessRegistrationDuplicateUsername() throws Exception {
        doThrow(new DuplicateResourceException("Tên đăng nhập 'admin' đã được sử dụng!"))
                .when(authService).register(any(RegisterRequestDto.class));

        mockMvc.perform(post("/register")
                        .param("username", "admin")
                        .param("password", "admin123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}
