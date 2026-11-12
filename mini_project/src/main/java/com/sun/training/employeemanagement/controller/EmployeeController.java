package com.sun.training.employeemanagement.controller;

import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller providing complete CRUD and search APIs for Employee management.
 */
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    /**
     * Constructor injection of EmployeeService.
     *
     * @param employeeService employee service bean
     */
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * API to retrieve all employees with optional query param filtering.
     *
     * @param department optional department name filter
     * @param keyword optional keyword search filter across code, name, and email
     * @return response entity with list of employees
     */
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String keyword) {
        LOGGER.info("GET /api/v1/employees requested (department='{}', keyword='{}')", department, keyword);
        List<EmployeeResponseDto> employees = employeeService.getAllEmployees(department, keyword);
        return ResponseEntity.ok(employees);
    }

    /**
     * API to retrieve an employee by employee code via Path Variable (e.g. N0001).
     *
     * @param employeeCode unique employee code
     * @return response entity with employee or 404 NOT FOUND
     */
    @GetMapping("/{employeeCode}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeByCode(@PathVariable String employeeCode) {
        LOGGER.info("GET /api/v1/employees/{} requested", employeeCode);
        return employeeService.getEmployeeByCode(employeeCode)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * API to search employees by name.
     *
     * @param name keyword matching employee name
     * @return list of matching employees
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<EmployeeResponseDto>> searchByName(@RequestParam String name) {
        LOGGER.info("GET /api/v1/employees/search/name requested with name='{}'", name);
        List<EmployeeResponseDto> results = employeeService.searchEmployeesByName(name);
        return ResponseEntity.ok(results);
    }

    /**
     * API to search employees by department name.
     *
     * @param name department name
     * @return list of matching employees
     */
    @GetMapping("/search/department")
    public ResponseEntity<List<EmployeeResponseDto>> searchByDepartment(@RequestParam String name) {
        LOGGER.info("GET /api/v1/employees/search/department requested with department='{}'", name);
        List<EmployeeResponseDto> results = employeeService.searchEmployeesByDepartment(name);
        return ResponseEntity.ok(results);
    }

    /**
     * API to create a new employee persisted to the database.
     *
     * @param requestDto validated employee request payload
     * @return response entity with created employee and status 201 CREATED
     */
    @PostMapping
    public ResponseEntity<EmployeeResponseDto> createEmployee(
            @Valid @RequestBody EmployeeRequestDto requestDto) {
        LOGGER.info("POST /api/v1/employees requested for email: {}", requestDto.getEmail());
        EmployeeResponseDto created = employeeService.createEmployee(requestDto);
        URI location = URI.create("/api/v1/employees/" + created.getEmployeeCode());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * API to update an existing employee by employee code.
     *
     * @param employeeCode unique employee code
     * @param requestDto updated employee information
     * @return response entity with updated employee or 404 NOT FOUND
     */
    @PutMapping("/{employeeCode}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(
            @PathVariable String employeeCode,
            @Valid @RequestBody EmployeeRequestDto requestDto) {
        LOGGER.info("PUT /api/v1/employees/{} requested", employeeCode);
        return employeeService.updateEmployee(employeeCode, requestDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * API to delete an employee by employee code.
     *
     * @param employeeCode unique employee code
     * @return 204 NO CONTENT on success or 404 NOT FOUND if employee does not exist
     */
    @DeleteMapping("/{employeeCode}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String employeeCode) {
        LOGGER.info("DELETE /api/v1/employees/{} requested", employeeCode);
        boolean deleted = employeeService.deleteEmployee(employeeCode);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
