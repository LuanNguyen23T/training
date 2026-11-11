package com.sun.training.employeemanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sun.training.employeemanagement.dto.EmployeeRegistrationDto;
import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.model.Employee;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private UtilityService utilityService;

    @Mock
    private ModelMapper modelMapper;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(utilityService, modelMapper);
    }

    @Test
    void testRegisterEmployee_success() {
        EmployeeRegistrationDto request = new EmployeeRegistrationDto(
                "  nguyen van a ",
                "nguyenvana@sun-asterisk.com",
                "Engineering"
        );

        EmployeeResponseDto mappedDto = new EmployeeResponseDto();
        mappedDto.setFullName("  nguyen van a ");
        mappedDto.setEmail("nguyenvana@sun-asterisk.com");
        mappedDto.setDepartment("Engineering");

        when(modelMapper.map(any(EmployeeRegistrationDto.class), eq(EmployeeResponseDto.class)))
                .thenReturn(mappedDto);
        when(utilityService.formatFullName("  nguyen van a ")).thenReturn("Nguyen Van A");
        when(utilityService.generateEmployeeCode()).thenReturn("N0001");

        EmployeeResponseDto result = employeeService.registerEmployee(request);

        assertNotNull(result);
        assertEquals("N0001", result.getEmployeeCode());
        assertEquals("Nguyen Van A", result.getFullName());
        assertEquals("nguyenvana@sun-asterisk.com", result.getEmail());
        assertEquals("Engineering", result.getDepartment());

        verify(modelMapper).map(request, EmployeeResponseDto.class);
        verify(utilityService).formatFullName("  nguyen van a ");
        verify(utilityService).generateEmployeeCode();
    }

    @Test
    void testInitSampleData_andGetAllEmployees() {
        when(utilityService.generateEmployeeCode())
                .thenReturn("N0001", "N0002", "N0003");
        when(utilityService.formatFullName(any(String.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(any(Employee.class), eq(EmployeeResponseDto.class)))
                .thenAnswer(invocation -> {
                    Employee emp = invocation.getArgument(0);
                    return new EmployeeResponseDto(
                            emp.getEmployeeCode(),
                            emp.getFullName(),
                            emp.getEmail(),
                            emp.getDepartment(),
                            emp.getSalary()
                    );
                });

        employeeService.initSampleData();

        List<EmployeeResponseDto> all = employeeService.getAllEmployees(null, null);
        assertEquals(3, all.size());
        assertEquals("N0001", all.get(0).getEmployeeCode());
    }

    @Test
    void testGetAllEmployees_filterByDepartmentAndKeyword() {
        when(utilityService.generateEmployeeCode())
                .thenReturn("N0001", "N0002", "N0003");
        when(utilityService.formatFullName(any(String.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(any(Employee.class), eq(EmployeeResponseDto.class)))
                .thenAnswer(invocation -> {
                    Employee emp = invocation.getArgument(0);
                    return new EmployeeResponseDto(
                            emp.getEmployeeCode(),
                            emp.getFullName(),
                            emp.getEmail(),
                            emp.getDepartment(),
                            emp.getSalary()
                    );
                });

        employeeService.initSampleData();

        List<EmployeeResponseDto> filtered = employeeService.getAllEmployees("Engineering", "Luan");
        assertEquals(1, filtered.size());
        assertEquals("Nguyen Huu Hoang Luan", filtered.get(0).getFullName());

        List<EmployeeResponseDto> emptyResult = employeeService.getAllEmployees("Marketing", null);
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    void testGetEmployeeByCode_foundAndNotFound() {
        when(utilityService.generateEmployeeCode()).thenReturn("N0001");
        when(utilityService.formatFullName("Nguyen Huu Hoang Luan")).thenReturn("Nguyen Huu Hoang Luan");

        when(modelMapper.map(any(Employee.class), eq(EmployeeResponseDto.class)))
                .thenAnswer(invocation -> {
                    Employee emp = invocation.getArgument(0);
                    return new EmployeeResponseDto(
                            emp.getEmployeeCode(),
                            emp.getFullName(),
                            emp.getEmail(),
                            emp.getDepartment(),
                            emp.getSalary()
                    );
                });

        EmployeeRequestDto request = new EmployeeRequestDto(
                "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", "Engineering", 15000000.0);
        EmployeeResponseDto created = employeeService.createEmployee(request);

        Optional<EmployeeResponseDto> found = employeeService.getEmployeeByCode(created.getEmployeeCode());
        assertTrue(found.isPresent());
        assertEquals("N0001", found.get().getEmployeeCode());
        assertEquals("Nguyen Huu Hoang Luan", found.get().getFullName());

        Optional<EmployeeResponseDto> notFound = employeeService.getEmployeeByCode("NOT_EXIST");
        assertFalse(notFound.isPresent());
    }

    @Test
    void testCreateEmployee_success() {
        when(utilityService.generateEmployeeCode()).thenReturn("N0001");
        when(utilityService.formatFullName("  le van cuong ")).thenReturn("Le Van Cuong");

        when(modelMapper.map(any(Employee.class), eq(EmployeeResponseDto.class)))
                .thenAnswer(invocation -> {
                    Employee emp = invocation.getArgument(0);
                    return new EmployeeResponseDto(
                            emp.getEmployeeCode(),
                            emp.getFullName(),
                            emp.getEmail(),
                            emp.getDepartment(),
                            emp.getSalary()
                    );
                });

        EmployeeRequestDto request = new EmployeeRequestDto(
                "  le van cuong ", "cuong.le@sun-asterisk.com", "Finance", 14000000.0);

        EmployeeResponseDto created = employeeService.createEmployee(request);

        assertNotNull(created);
        assertEquals("N0001", created.getEmployeeCode());
        assertEquals("Le Van Cuong", created.getFullName());
        assertEquals("Finance", created.getDepartment());
        assertEquals(14000000.0, created.getSalary());
    }
}
