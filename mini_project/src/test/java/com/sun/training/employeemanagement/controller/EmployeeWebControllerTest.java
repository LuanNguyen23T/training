package com.sun.training.employeemanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.entity.Department;
import com.sun.training.employeemanagement.repository.DepartmentRepository;
import com.sun.training.employeemanagement.service.EmployeeService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class EmployeeWebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        EmployeeWebController controller = new EmployeeWebController(employeeService, departmentRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testListEmployees_returnsListViewAndModel() throws Exception {
        EmployeeResponseDto emp = new EmployeeResponseDto(
                "N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", "Engineering", 15000000.0);
        Department dept = new Department("Engineering", "Software");

        when(employeeService.getAllEmployees(null, null)).thenReturn(List.of(emp));
        when(departmentRepository.findAll()).thenReturn(List.of(dept));

        mockMvc.perform(get("/employees/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/list"))
                .andExpect(model().attributeExists("employees", "departments"));
    }

    @Test
    void testShowAddForm_returnsAddViewAndEmptyEmployee() throws Exception {
        Department dept = new Department("Engineering", "Software");
        when(departmentRepository.findAll()).thenReturn(List.of(dept));

        mockMvc.perform(get("/employees/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/add"))
                .andExpect(model().attributeExists("employee", "departments"));
    }

    @Test
    void testProcessAddEmployee_validInput_redirectsToList() throws Exception {
        EmployeeResponseDto created = new EmployeeResponseDto(
                "N0004", "Vo Thi Anh Tuyet", "tuyet.vo@sun-asterisk.com", "Human Resources", 13000000.0);

        when(employeeService.createEmployee(any(EmployeeRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/employees/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fullName", "Vo Thi Anh Tuyet")
                        .param("email", "tuyet.vo@sun-asterisk.com")
                        .param("department", "Human Resources")
                        .param("salary", "13000000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees/list"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testProcessAddEmployee_invalidInput_returnsAddViewWithErrors() throws Exception {
        Department dept = new Department("Engineering", "Software");
        when(departmentRepository.findAll()).thenReturn(List.of(dept));

        mockMvc.perform(post("/employees/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fullName", "")
                        .param("email", "invalid-email")
                        .param("department", "")
                        .param("salary", "-100"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/add"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testShowEditForm_returnsEditView() throws Exception {
        EmployeeResponseDto emp = new EmployeeResponseDto(
                "N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", "Engineering", 15000000.0);
        Department dept = new Department("Engineering", "Software");

        when(employeeService.getEmployeeByCode("N0001")).thenReturn(emp);
        when(departmentRepository.findAll()).thenReturn(List.of(dept));

        mockMvc.perform(get("/employees/edit/N0001"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/edit"))
                .andExpect(model().attributeExists("employee", "departments", "employeeCode"));
    }

    @Test
    void testProcessEditEmployee_validInput_redirectsToList() throws Exception {
        EmployeeResponseDto updated = new EmployeeResponseDto(
                "N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", "Engineering", 18000000.0);

        when(employeeService.updateEmployee(any(String.class), any(EmployeeRequestDto.class))).thenReturn(updated);

        mockMvc.perform(post("/employees/edit/N0001")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fullName", "Nguyen Huu Hoang Luan")
                        .param("email", "luan.nguyen@sun-asterisk.com")
                        .param("department", "Engineering")
                        .param("salary", "18000000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees/list"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testProcessEditEmployee_invalidInput_returnsEditViewWithErrors() throws Exception {
        Department dept = new Department("Engineering", "Software");
        when(departmentRepository.findAll()).thenReturn(List.of(dept));

        mockMvc.perform(post("/employees/edit/N0001")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fullName", "")
                        .param("email", "bad-email")
                        .param("department", "")
                        .param("salary", "-500"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/edit"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("employeeCode", "departments"));
    }

    @Test
    void testSearchEmployees_initialView_returnsEmptyResults() throws Exception {
        Department dept = new Department("Engineering", "Software");
        when(departmentRepository.findAll()).thenReturn(List.of(dept));

        mockMvc.perform(get("/employees/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/search"))
                .andExpect(model().attribute("hasSearched", false))
                .andExpect(model().attributeExists("results", "departments"));
    }

    @Test
    void testSearchEmployees_withParameters_returnsResults() throws Exception {
        EmployeeResponseDto emp = new EmployeeResponseDto(
                "N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", "Engineering", 15000000.0);
        Department dept = new Department("Engineering", "Software");

        when(employeeService.getAllEmployees(null, "Luan")).thenReturn(List.of(emp));
        when(departmentRepository.findAll()).thenReturn(List.of(dept));

        mockMvc.perform(get("/employees/search").param("name", "Luan"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/search"))
                .andExpect(model().attribute("hasSearched", true))
                .andExpect(model().attributeExists("results"));
    }

    @Test
    void testDeleteEmployee_redirectsToListWithFlashMessage() throws Exception {
        doNothing().when(employeeService).deleteEmployee("N0001");

        mockMvc.perform(get("/employees/delete/N0001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(employeeService).deleteEmployee("N0001");
    }

    @Test
    void testCheckEmailWebEndpoint() throws Exception {
        when(employeeService.isEmailExists("luan@sun.com", null)).thenReturn(true);

        mockMvc.perform(get("/employees/check-email").param("email", "luan@sun.com"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.exists").value(true));
    }
}

