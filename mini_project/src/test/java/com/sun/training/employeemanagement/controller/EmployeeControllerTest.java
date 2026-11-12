package com.sun.training.employeemanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    void testSearchByName_returnsMatchingList() throws Exception {
        EmployeeResponseDto emp = new EmployeeResponseDto(
                "N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", "Engineering", 15000000.0);

        when(employeeService.searchEmployeesByName("Luan")).thenReturn(List.of(emp));

        mockMvc.perform(get("/api/v1/employees/search/name").param("name", "Luan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Nguyen Huu Hoang Luan"));
    }

    @Test
    void testSearchByDepartment_returnsMatchingList() throws Exception {
        EmployeeResponseDto emp = new EmployeeResponseDto(
                "N0002", "Tran Thi Binh", "binh.tran@sun-asterisk.com", "Human Resources", 12000000.0);

        when(employeeService.searchEmployeesByDepartment("Human Resources")).thenReturn(List.of(emp));

        mockMvc.perform(get("/api/v1/employees/search/department").param("name", "Human Resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].department").value("Human Resources"));
    }

    @Test
    void testCreateEmployee_validInput_returns201AndCreated() throws Exception {
        String requestJson = """
                {
                    "fullName": "Le Van Cuong",
                    "email": "cuong.le@sun-asterisk.com",
                    "department": "Finance",
                    "salary": 14000000
                }
                """;

        EmployeeResponseDto createdDto = new EmployeeResponseDto(
                "N0003", "Le Van Cuong", "cuong.le@sun-asterisk.com", "Finance", 14000000.0);

        when(employeeService.createEmployee(any(EmployeeRequestDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/employees/N0003"))
                .andExpect(jsonPath("$.employeeCode").value("N0003"))
                .andExpect(jsonPath("$.fullName").value("Le Van Cuong"))
                .andExpect(jsonPath("$.email").value("cuong.le@sun-asterisk.com"))
                .andExpect(jsonPath("$.department").value("Finance"))
                .andExpect(jsonPath("$.salary").value(14000000.0));
    }

    @Test
    void testUpdateEmployee_whenFound_returns200() throws Exception {
        String requestJson = """
                {
                    "fullName": "Nguyen Huu Hoang Luan Updated",
                    "email": "luan.updated@sun-asterisk.com",
                    "department": "Engineering",
                    "salary": 18000000
                }
                """;

        EmployeeResponseDto updatedDto = new EmployeeResponseDto(
                "N0001", "Nguyen Huu Hoang Luan Updated", "luan.updated@sun-asterisk.com", "Engineering", 18000000.0);

        when(employeeService.updateEmployee(eq("N0001"), any(EmployeeRequestDto.class)))
                .thenReturn(Optional.of(updatedDto));

        mockMvc.perform(put("/api/v1/employees/N0001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Nguyen Huu Hoang Luan Updated"))
                .andExpect(jsonPath("$.salary").value(18000000.0));
    }

    @Test
    void testUpdateEmployee_whenNotFound_returns404() throws Exception {
        String requestJson = """
                {
                    "fullName": "Someone",
                    "email": "someone@sun-asterisk.com",
                    "department": "Engineering",
                    "salary": 10000000
                }
                """;

        when(employeeService.updateEmployee(eq("NOT_EXIST"), any(EmployeeRequestDto.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/employees/NOT_EXIST")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEmployee_whenFound_returns204() throws Exception {
        when(employeeService.deleteEmployee("N0001")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/employees/N0001"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteEmployee_whenNotFound_returns404() throws Exception {
        when(employeeService.deleteEmployee("NOT_EXIST")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/employees/NOT_EXIST"))
                .andExpect(status().isNotFound());
    }
}
