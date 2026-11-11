package com.sun.training.employeemanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.service.EmployeeService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        EmployeeController controller = new EmployeeController(employeeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAllEmployees_returnsListAnd200() throws Exception {
        EmployeeResponseDto emp1 = new EmployeeResponseDto(
                "N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", "Engineering", 15000000.0);
        EmployeeResponseDto emp2 = new EmployeeResponseDto(
                "N0002", "Tran Thi Binh", "binh.tran@sun-asterisk.com", "HR", 12000000.0);

        when(employeeService.getAllEmployees(null, null)).thenReturn(List.of(emp1, emp2));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].employeeCode").value("N0001"))
                .andExpect(jsonPath("$[0].fullName").value("Nguyen Huu Hoang Luan"))
                .andExpect(jsonPath("$[1].employeeCode").value("N0002"))
                .andExpect(jsonPath("$[1].fullName").value("Tran Thi Binh"));
    }

    @Test
    void testGetAllEmployees_withQueryParams_returnsFilteredList() throws Exception {
        EmployeeResponseDto emp = new EmployeeResponseDto(
                "N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", "Engineering", 15000000.0);

        when(employeeService.getAllEmployees("Engineering", "Luan")).thenReturn(List.of(emp));

        mockMvc.perform(get("/api/v1/employees")
                        .param("department", "Engineering")
                        .param("keyword", "Luan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Nguyen Huu Hoang Luan"))
                .andExpect(jsonPath("$[0].department").value("Engineering"));
    }

    @Test
    void testGetEmployeeByCode_whenFound_returns200() throws Exception {
        EmployeeResponseDto emp = new EmployeeResponseDto(
                "N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", "Engineering", 15000000.0);

        when(employeeService.getEmployeeByCode("N0001")).thenReturn(Optional.of(emp));

        mockMvc.perform(get("/api/v1/employees/N0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode").value("N0001"))
                .andExpect(jsonPath("$.fullName").value("Nguyen Huu Hoang Luan"))
                .andExpect(jsonPath("$.email").value("luan.nguyen@sun-asterisk.com"));
    }

    @Test
    void testGetEmployeeByCode_whenNotFound_returns404() throws Exception {
        when(employeeService.getEmployeeByCode("NON_EXIST")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/employees/NON_EXIST"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateEmployee_validInput_returns201AndCreated() throws Exception {
        String requestJson = """
                {
                    "fullName": "Le Van Cuong",
                    "email": "cuong.le@sun-asterisk.com",
                    "department": "Finance",
                    "salary": 14000000.0
                }
                """;

        EmployeeResponseDto created = new EmployeeResponseDto(
                "N0003", "Le Van Cuong", "cuong.le@sun-asterisk.com", "Finance", 14000000.0);

        when(employeeService.createEmployee(any(EmployeeRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/employees/N0003"))
                .andExpect(jsonPath("$.employeeCode").value("N0003"))
                .andExpect(jsonPath("$.fullName").value("Le Van Cuong"))
                .andExpect(jsonPath("$.salary").value(14000000.0));
    }

    @Test
    void testCreateEmployee_invalidInput_returns400BadRequest() throws Exception {
        String invalidJson = """
                {
                    "fullName": "",
                    "email": "invalid-email-format",
                    "department": "",
                    "salary": -5000.0
                }
                """;

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
