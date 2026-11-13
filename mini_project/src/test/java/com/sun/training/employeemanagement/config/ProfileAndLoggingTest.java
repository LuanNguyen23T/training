package com.sun.training.employeemanagement.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.entity.Department;
import com.sun.training.employeemanagement.entity.Employee;
import com.sun.training.employeemanagement.exception.DuplicateResourceException;
import com.sun.training.employeemanagement.exception.ResourceNotFoundException;
import com.sun.training.employeemanagement.repository.DepartmentRepository;
import com.sun.training.employeemanagement.repository.EmployeeRepository;
import com.sun.training.employeemanagement.service.EmployeeService;
import com.sun.training.employeemanagement.service.UtilityService;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;

/**
 * Unit and Logging tests for Lab 7 (Logging & Profiles).
 * Verifies that SLF4J / Logback accurately records INFO and WARN logs
 * during add, update, and delete employee workflows.
 */
@ExtendWith(MockitoExtension.class)
class ProfileAndLoggingTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UtilityService utilityService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger serviceLogger;

    @BeforeEach
    void setUp() {
        serviceLogger = (Logger) LoggerFactory.getLogger(EmployeeService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        serviceLogger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        serviceLogger.detachAppender(listAppender);
        listAppender.stop();
    }

    @Test
    @DisplayName("Should log INFO when creating an employee successfully")
    void testLoggingOnCreateEmployeeSuccess() {
        EmployeeRequestDto request = new EmployeeRequestDto();
        request.setFullName("Nguyen Van An");
        request.setEmail("an.nguyen@sun-asterisk.com");
        request.setDepartment("IT");
        request.setSalary(15000000.0);

        when(employeeRepository.existsByEmail("an.nguyen@sun-asterisk.com")).thenReturn(false);
        when(utilityService.formatFullName("Nguyen Van An")).thenReturn("Nguyen Van An");
        when(utilityService.generateEmployeeCode()).thenReturn("N0010");
        when(employeeRepository.existsByEmployeeCode("N0010")).thenReturn(false);

        Department dept = new Department("IT", "Information Technology");
        when(departmentRepository.findByNameIgnoreCase("IT")).thenReturn(Optional.of(dept));

        Employee saved = new Employee("N0010", "Nguyen Van An", "an.nguyen@sun-asterisk.com", 15000000.0, dept);
        when(employeeRepository.save(any(Employee.class))).thenReturn(saved);

        EmployeeResponseDto response = employeeService.createEmployee(request);

        assertNotNull(response);
        assertTrue(listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.INFO
                        && event.getFormattedMessage().contains("Starting employee creation process")
                        && event.getFormattedMessage().contains("an.nguyen@sun-asterisk.com")),
                "Should log INFO when starting employee creation");

        assertTrue(listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.INFO
                        && event.getFormattedMessage().contains("Employee created successfully")
                        && event.getFormattedMessage().contains("N0010")),
                "Should log INFO when employee is created successfully");
    }

    @Test
    @DisplayName("Should log WARN when employee creation fails due to duplicate email")
    void testLoggingOnCreateEmployeeDuplicateEmail() {
        EmployeeRequestDto request = new EmployeeRequestDto();
        request.setFullName("Nguyen Van An");
        request.setEmail("duplicate@sun-asterisk.com");
        request.setDepartment("IT");
        request.setSalary(15000000.0);

        when(employeeRepository.existsByEmail("duplicate@sun-asterisk.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> employeeService.createEmployee(request));

        assertTrue(listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.WARN
                        && event.getFormattedMessage().contains("Failed to create employee")
                        && event.getFormattedMessage().contains("duplicate@sun-asterisk.com")),
                "Should log WARN when employee creation encounters duplicate email");
    }

    @Test
    @DisplayName("Should log INFO when updating employee successfully")
    void testLoggingOnUpdateEmployeeSuccess() {
        String code = "N0001";
        EmployeeRequestDto request = new EmployeeRequestDto();
        request.setFullName("Nguyen Van B");
        request.setEmail("b.nguyen@sun-asterisk.com");
        request.setDepartment("HR");
        request.setSalary(18000000.0);

        Department dept = new Department("HR", "Human Resources");
        Employee existing = new Employee(code, "Nguyen Van A", "a.nguyen@sun-asterisk.com", 12000000.0, dept);

        when(employeeRepository.findByEmployeeCode(code)).thenReturn(Optional.of(existing));
        when(employeeRepository.existsByEmail("b.nguyen@sun-asterisk.com")).thenReturn(false);
        when(utilityService.formatFullName("Nguyen Van B")).thenReturn("Nguyen Van B");
        when(departmentRepository.findByNameIgnoreCase("HR")).thenReturn(Optional.of(dept));
        when(employeeRepository.save(any(Employee.class))).thenReturn(existing);

        EmployeeResponseDto response = employeeService.updateEmployee(code, request);

        assertNotNull(response);
        assertTrue(listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.INFO
                        && event.getFormattedMessage().contains("Starting employee update process")
                        && event.getFormattedMessage().contains(code)),
                "Should log INFO when starting update process");

        assertTrue(listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.INFO
                        && event.getFormattedMessage().contains("Employee updated successfully")
                        && event.getFormattedMessage().contains(code)),
                "Should log INFO when employee is updated successfully");
    }

    @Test
    @DisplayName("Should log WARN when updating non-existent employee")
    void testLoggingOnUpdateEmployeeNotFound() {
        String nonExistentCode = "N9999";
        EmployeeRequestDto request = new EmployeeRequestDto();
        request.setFullName("Non Existent");
        request.setEmail("none@sun-asterisk.com");
        request.setDepartment("IT");
        request.setSalary(10000000.0);

        when(employeeRepository.findByEmployeeCode(nonExistentCode)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(nonExistentCode, request));

        assertTrue(listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.WARN
                        && event.getFormattedMessage().contains("Failed to update employee")
                        && event.getFormattedMessage().contains(nonExistentCode)),
                "Should log WARN when employee to update is not found");
    }

    @Test
    @DisplayName("Should log INFO when deleting employee successfully")
    void testLoggingOnDeleteEmployeeSuccess() {
        String code = "N0001";
        Department dept = new Department("Engineering", "Tech");
        Employee existing = new Employee(code, "Luan Nguyen", "luan@sun-asterisk.com", 20000000.0, dept);

        when(employeeRepository.findByEmployeeCode(code)).thenReturn(Optional.of(existing));

        employeeService.deleteEmployee(code);

        verify(employeeRepository).delete(existing);
        assertTrue(listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.INFO
                        && event.getFormattedMessage().contains("Starting employee deletion process")
                        && event.getFormattedMessage().contains(code)),
                "Should log INFO when starting deletion process");

        assertTrue(listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.INFO
                        && event.getFormattedMessage().contains("deleted successfully")
                        && event.getFormattedMessage().contains(code)),
                "Should log INFO when employee is deleted successfully");
    }

    @Test
    @DisplayName("Should log WARN when deleting non-existent employee")
    void testLoggingOnDeleteEmployeeNotFound() {
        String nonExistentCode = "N9999";
        when(employeeRepository.findByEmployeeCode(nonExistentCode)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(nonExistentCode));

        assertTrue(listAppender.list.stream().anyMatch(event ->
                event.getLevel() == Level.WARN
                        && event.getFormattedMessage().contains("Failed to delete employee")
                        && event.getFormattedMessage().contains(nonExistentCode)),
                "Should log WARN when employee to delete is not found");
    }
}
