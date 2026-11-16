package com.sun.training.employeemanagement.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for Role-Based Access Control (RBAC) (Module 9).
 * Verifies that USER can only view employees while ADMIN can perform full CRUD operations.
 */
@SpringBootTest
@ActiveProfiles("dev")
class SecurityAuthorizationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EmployeeRepository employeeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Module 9: Unauthenticated request to /api/v1/employees returns 401 Unauthorized")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Module 9: USER role can view employee list (200 OK)")
    void shouldAllowUserToViewEmployeeList() throws Exception {
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Module 9: USER role is FORBIDDEN (403) from creating employee")
    void shouldDenyUserFromCreatingEmployee() throws Exception {
        EmployeeRequestDto request = new EmployeeRequestDto();
        request.setFullName("Unauthorized Creation");
        request.setEmail("unauth@sun-asterisk.com");
        request.setDepartment("Engineering");
        request.setSalary(10000000.0);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Module 9: USER role is FORBIDDEN (403) from deleting employee")
    void shouldDenyUserFromDeletingEmployee() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/N0001"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Module 9: ADMIN role CAN create new employee (201 Created)")
    void shouldAllowAdminToCreateEmployee() throws Exception {
        EmployeeRequestDto request = new EmployeeRequestDto();
        request.setFullName("Admin Created User");
        request.setEmail("admin.created." + System.currentTimeMillis() + "@sun-asterisk.com");
        request.setDepartment("Engineering");
        request.setSalary(25000000.0);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Admin Created User"));

        // Cleanup: remove created test employee immediately so DB stays clean
        employeeRepository.findByEmail(request.getEmail())
                .ifPresent(employeeRepository::delete);
    }

    @Test
    @DisplayName("Module 9: HTTP Basic Authentication works with valid credentials")
    void shouldAuthenticateWithHttpBasic() throws Exception {
        mockMvc.perform(get("/api/v1/employees")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }
}
