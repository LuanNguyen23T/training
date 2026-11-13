package com.sun.training.employeemanagement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for Spring Boot Actuator and Report endpoints (Module 8).
 */
@SpringBootTest
@ActiveProfiles("dev")
class ActuatorIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Module 8: Actuator health endpoint returns UP status")
    void shouldReturnHealthStatusUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Module 8: Actuator metrics endpoint is exposed and accessible")
    void shouldExposeMetricsEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray());
    }

    @Test
    @DisplayName("Module 8: Total employee count report API returns 200 with cache metadata")
    void shouldReturnTotalEmployeeCountReport() throws Exception {
        mockMvc.perform(get("/api/v1/employees/reports/total-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").isNumber())
                .andExpect(jsonPath("$.cachedTtl").value("60s"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
