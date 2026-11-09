package com.sun.training.employeemanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sun.training.employeemanagement.dto.EmployeeRegistrationDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.service.EmployeeService;
import com.sun.training.employeemanagement.service.UtilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class EmployeeDemoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UtilityService utilityService;

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        EmployeeDemoController controller = new EmployeeDemoController(utilityService, employeeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGenerateEmployeeCode() throws Exception {
        when(utilityService.generateEmployeeCode()).thenReturn("EMP-00001");

        mockMvc.perform(get("/api/v1/demo/generate-code"))
                .andExpect(status().isOk())
                .andExpect(content().string("EMP-00001"));
    }

    @Test
    void testFormatName() throws Exception {
        when(utilityService.formatFullName("nguyen van a")).thenReturn("Nguyen Van A");

        mockMvc.perform(get("/api/v1/demo/format-name")
                        .param("name", "nguyen van a"))
                .andExpect(status().isOk())
                .andExpect(content().string("Nguyen Van A"));
    }

    @Test
    void testRegisterEmployee() throws Exception {
        String requestJson = """
                {
                    "fullName": "nguyen van a",
                    "email": "nguyenvana@sun-asterisk.com",
                    "department": "Engineering"
                }
                """;

        EmployeeResponseDto response = new EmployeeResponseDto(
                "EMP-00001",
                "Nguyen Van A",
                "nguyenvana@sun-asterisk.com",
                "Engineering"
        );

        when(employeeService.registerEmployee(any(EmployeeRegistrationDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/demo/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode").value("EMP-00001"))
                .andExpect(jsonPath("$.fullName").value("Nguyen Van A"))
                .andExpect(jsonPath("$.email").value("nguyenvana@sun-asterisk.com"))
                .andExpect(jsonPath("$.department").value("Engineering"));
    }
}
