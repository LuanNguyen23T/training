package com.sun.training.employeemanagement.controller;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.sun.training.employeemanagement.dto.DepartmentStatisticDto;
import com.sun.training.employeemanagement.dto.EmployeeStatisticsDto;
import com.sun.training.employeemanagement.repository.DepartmentRepository;
import com.sun.training.employeemanagement.repository.EmployeeRepository;
import com.sun.training.employeemanagement.service.EmployeeService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for Module 10: Reporting & Analytics (Thống Kê).
 * Verifies repository queries, REST API endpoints, and web view rendering.
 */
@SpringBootTest
@ActiveProfiles("dev")
class EmployeeStatisticsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeService employeeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Module 10 Repository: countTotalEmployees @Query returns total employee count")
    void testCountTotalEmployeesQuery() {
        long count = employeeRepository.countTotalEmployees();
        assertTrue(count >= 3, "Total employees should be at least 3 initial seeded employees");
    }

    @Test
    @DisplayName("Module 10 Repository: countEmployeesByDepartment @Query aggregates count and average salary")
    void testCountEmployeesByDepartmentQuery() {
        List<DepartmentStatisticDto> stats = employeeRepository.countEmployeesByDepartment();
        assertNotNull(stats);
        assertFalse(stats.isEmpty(), "Department statistics should not be empty");
        for (DepartmentStatisticDto stat : stats) {
            assertNotNull(stat.getDepartmentName());
            assertTrue(stat.getEmployeeCount() > 0);
        }
    }

    @Test
    @DisplayName("Module 10 Repository: countEmployeesPerDepartment @Query in DepartmentRepository returns all departments")
    void testCountEmployeesPerDepartmentQuery() {
        List<DepartmentStatisticDto> stats = departmentRepository.countEmployeesPerDepartment();
        assertNotNull(stats);
        assertEquals(departmentRepository.count(), stats.size(), "Should return statistics for all departments in DB");
    }

    @Test
    @DisplayName("Module 10 Repository: salary calculation @Query methods compute correct averages and bounds")
    void testSalaryAggregationQueries() {
        Double avg = employeeRepository.calculateAverageSalary();
        Double max = employeeRepository.findMaxSalary();
        Double min = employeeRepository.findMinSalary();

        assertNotNull(avg);
        assertNotNull(max);
        assertNotNull(min);
        assertTrue(max >= avg, "Max salary should be greater than or equal to average salary");
        assertTrue(avg >= min, "Average salary should be greater than or equal to min salary");
    }

    @Test
    @DisplayName("Module 10 Service: getOverallStatistics returns populated EmployeeStatisticsDto")
    void testServiceOverallStatistics() {
        EmployeeStatisticsDto stats = employeeService.getOverallStatistics();
        assertNotNull(stats);
        assertTrue(stats.getTotalEmployees() >= 3);
        assertEquals(departmentRepository.count(), stats.getTotalDepartments());
        assertNotNull(stats.getByDepartment());
        assertFalse(stats.getByDepartment().isEmpty());
    }

    @Test
    @DisplayName("Module 10 REST API: Unauthenticated request to /api/v1/employees/statistics returns 401")
    void testGetStatisticsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/employees/statistics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Module 10 REST API: USER role can access /api/v1/employees/statistics (200 OK)")
    void testGetStatisticsAsUser() throws Exception {
        int deptCount = (int) departmentRepository.count();
        mockMvc.perform(get("/api/v1/employees/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees", greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.totalDepartments", is(deptCount)))
                .andExpect(jsonPath("$.averageSalary").isNumber())
                .andExpect(jsonPath("$.byDepartment", hasSize(deptCount)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Module 10 REST API: ADMIN role can access /api/v1/employees/statistics/by-department (200 OK)")
    void testGetDepartmentStatisticsAsAdmin() throws Exception {
        int deptCount = (int) departmentRepository.count();
        mockMvc.perform(get("/api/v1/employees/statistics/by-department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(deptCount)))
                .andExpect(jsonPath("$[0].departmentName").isNotEmpty())
                .andExpect(jsonPath("$[0].employeeCount").isNumber())
                .andExpect(jsonPath("$[0].percentage").isNumber());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Module 10 REST API: /api/v1/employees/statistics/total returns total employee count")
    void testGetTotalEmployeesCountApi() throws Exception {
        mockMvc.perform(get("/api/v1/employees/statistics/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees", greaterThanOrEqualTo(3)));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Module 10 Web View: GET /employees/statistics renders employee/statistics view (200 OK)")
    void testRenderStatisticsWebView() throws Exception {
        mockMvc.perform(get("/employees/statistics"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/statistics"))
                .andExpect(model().attributeExists("totalEmployees"))
                .andExpect(model().attributeExists("totalDepartments"))
                .andExpect(model().attributeExists("averageSalary"))
                .andExpect(model().attributeExists("departmentStats"));
    }
}
