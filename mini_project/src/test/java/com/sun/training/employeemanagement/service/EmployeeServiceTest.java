package com.sun.training.employeemanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sun.training.employeemanagement.dto.EmployeeRegistrationDto;
import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.entity.Department;
import com.sun.training.employeemanagement.entity.Employee;
import com.sun.training.employeemanagement.exception.DuplicateResourceException;
import com.sun.training.employeemanagement.exception.ResourceNotFoundException;
import com.sun.training.employeemanagement.repository.DepartmentRepository;
import com.sun.training.employeemanagement.repository.EmployeeRepository;
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
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UtilityService utilityService;

    @Mock
    private ModelMapper modelMapper;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(
                employeeRepository,
                departmentRepository,
                utilityService,
                modelMapper
        );
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
    void testGetAllEmployees_callsSearchEmployees() {
        Department dept = new Department("Engineering", "Tech");
        Employee emp1 = new Employee("N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", 15000000.0, dept);

        when(employeeRepository.searchEmployees("Engineering", "Luan")).thenReturn(List.of(emp1));

        List<EmployeeResponseDto> result = employeeService.getAllEmployees("Engineering", "Luan");

        assertEquals(1, result.size());
        assertEquals("N0001", result.get(0).getEmployeeCode());
        assertEquals("Nguyen Huu Hoang Luan", result.get(0).getFullName());
        assertEquals("Engineering", result.get(0).getDepartment());
    }

    @Test
    void testGetEmployeeByCode_found() {
        Department dept = new Department("Engineering", "Tech");
        Employee emp = new Employee("N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", 15000000.0, dept);

        when(employeeRepository.findByEmployeeCode("N0001")).thenReturn(Optional.of(emp));

        EmployeeResponseDto result = employeeService.getEmployeeByCode("N0001");

        assertNotNull(result);
        assertEquals("N0001", result.getEmployeeCode());
        assertEquals("Nguyen Huu Hoang Luan", result.getFullName());
    }

    @Test
    void testGetEmployeeByCode_notFound_throwsResourceNotFoundException() {
        when(employeeRepository.findByEmployeeCode("NOT_EXIST")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeByCode("NOT_EXIST"));
    }

    @Test
    void testCreateEmployee_success() {
        EmployeeRequestDto request = new EmployeeRequestDto(
                "  vo thi anh tuyet ",
                "tuyet.vo@sun-asterisk.com",
                "Human Resources",
                13000000.0
        );

        Department hr = new Department("Human Resources", "People");
        when(employeeRepository.existsByEmail("tuyet.vo@sun-asterisk.com")).thenReturn(false);
        when(utilityService.formatFullName("  vo thi anh tuyet ")).thenReturn("Vo Thi Anh Tuyet");
        when(utilityService.generateEmployeeCode()).thenReturn("N0004");
        when(employeeRepository.existsByEmployeeCode("N0004")).thenReturn(false);
        when(departmentRepository.findByNameIgnoreCase("Human Resources")).thenReturn(Optional.of(hr));

        Employee saved = new Employee("N0004", "Vo Thi Anh Tuyet", "tuyet.vo@sun-asterisk.com", 13000000.0, hr);
        when(employeeRepository.save(any(Employee.class))).thenReturn(saved);

        EmployeeResponseDto result = employeeService.createEmployee(request);

        assertNotNull(result);
        assertEquals("N0004", result.getEmployeeCode());
        assertEquals("Vo Thi Anh Tuyet", result.getFullName());
        assertEquals("Human Resources", result.getDepartment());
        assertEquals(13000000.0, result.getSalary());

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void testCreateEmployee_duplicateEmail_throwsDuplicateResourceException() {
        EmployeeRequestDto request = new EmployeeRequestDto(
                "Le Van Cuong",
                "cuong.le@sun-asterisk.com",
                "Finance",
                14000000.0
        );

        when(employeeRepository.existsByEmail("cuong.le@sun-asterisk.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> employeeService.createEmployee(request));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_success() {
        Department dept = new Department("Engineering", "Tech");
        Employee existing = new Employee("N0001", "Old Name", "old@sun-asterisk.com", 10000000.0, dept);

        EmployeeRequestDto updateReq = new EmployeeRequestDto(
                "Nguyen Huu Hoang Luan",
                "luan.nguyen@sun-asterisk.com",
                "Engineering",
                16000000.0
        );

        when(employeeRepository.findByEmployeeCode("N0001")).thenReturn(Optional.of(existing));
        when(employeeRepository.existsByEmail("luan.nguyen@sun-asterisk.com")).thenReturn(false);
        when(utilityService.formatFullName("Nguyen Huu Hoang Luan")).thenReturn("Nguyen Huu Hoang Luan");
        when(departmentRepository.findByNameIgnoreCase("Engineering")).thenReturn(Optional.of(dept));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponseDto updated = employeeService.updateEmployee("N0001", updateReq);

        assertNotNull(updated);
        assertEquals("Nguyen Huu Hoang Luan", updated.getFullName());
        assertEquals("luan.nguyen@sun-asterisk.com", updated.getEmail());
        assertEquals(16000000.0, updated.getSalary());
    }

    @Test
    void testUpdateEmployee_notFound_throwsResourceNotFoundException() {
        EmployeeRequestDto updateReq = new EmployeeRequestDto(
                "Nguyen Huu Hoang Luan",
                "luan.nguyen@sun-asterisk.com",
                "Engineering",
                16000000.0
        );

        when(employeeRepository.findByEmployeeCode("NOT_EXIST")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee("NOT_EXIST", updateReq));
    }

    @Test
    void testUpdateEmployee_duplicateEmail_throwsDuplicateResourceException() {
        Department dept = new Department("Engineering", "Tech");
        Employee existing = new Employee("N0001", "Old Name", "old@sun-asterisk.com", 10000000.0, dept);

        EmployeeRequestDto updateReq = new EmployeeRequestDto(
                "Nguyen Huu Hoang Luan",
                "different@sun-asterisk.com",
                "Engineering",
                16000000.0
        );

        when(employeeRepository.findByEmployeeCode("N0001")).thenReturn(Optional.of(existing));
        when(employeeRepository.existsByEmail("different@sun-asterisk.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> employeeService.updateEmployee("N0001", updateReq));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testDeleteEmployee_success() {
        Department dept = new Department("Engineering", "Tech");
        Employee existing = new Employee("N0001", "Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", 15000000.0, dept);

        when(employeeRepository.findByEmployeeCode("N0001")).thenReturn(Optional.of(existing));

        employeeService.deleteEmployee("N0001");

        verify(employeeRepository).delete(existing);
    }

    @Test
    void testDeleteEmployee_notFound_throwsResourceNotFoundException() {
        when(employeeRepository.findByEmployeeCode("NOT_EXIST")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee("NOT_EXIST"));
        verify(employeeRepository, never()).delete(any());
    }

    @Test
    void testSearchEmployeesByName_andDepartment() {
        Department hr = new Department("Human Resources", "People");
        Employee emp = new Employee("N0002", "Tran Thi Binh", "binh.tran@sun-asterisk.com", 12000000.0, hr);

        when(employeeRepository.findByNameContainingIgnoreCase("Binh")).thenReturn(List.of(emp));
        when(employeeRepository.findByDepartmentNameIgnoreCase("Human Resources")).thenReturn(List.of(emp));

        List<EmployeeResponseDto> byName = employeeService.searchEmployeesByName("Binh");
        assertEquals(1, byName.size());
        assertEquals("Tran Thi Binh", byName.get(0).getFullName());

        List<EmployeeResponseDto> byDept = employeeService.searchEmployeesByDepartment("Human Resources");
        assertEquals(1, byDept.size());
        assertEquals("Human Resources", byDept.get(0).getDepartment());
    }

    @Test
    void testIsEmailExists_variousScenarios() {
        Department dept = new Department("Engineering", "Tech");
        Employee emp = new Employee("N0001", "Existing", "existing@sun.com", 10000000.0, dept);

        when(employeeRepository.findByEmail("existing@sun.com")).thenReturn(Optional.of(emp));
        when(employeeRepository.findByEmail("notfound@sun.com")).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertTrue(employeeService.isEmailExists("existing@sun.com", null));
        org.junit.jupiter.api.Assertions.assertFalse(employeeService.isEmailExists("existing@sun.com", "N0001"));
        org.junit.jupiter.api.Assertions.assertTrue(employeeService.isEmailExists("existing@sun.com", "N0002"));
        org.junit.jupiter.api.Assertions.assertFalse(employeeService.isEmailExists("notfound@sun.com", null));
        org.junit.jupiter.api.Assertions.assertFalse(employeeService.isEmailExists("", null));
        org.junit.jupiter.api.Assertions.assertFalse(employeeService.isEmailExists(null, null));
    }
}
