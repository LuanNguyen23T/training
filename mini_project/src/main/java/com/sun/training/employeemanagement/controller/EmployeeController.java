package com.sun.training.employeemanagement.controller;

import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
 * REST Controller providing complete CRUD and search APIs for Employee management with validation.
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
     * @return response entity with employee (throws ResourceNotFoundException if not found)
     */
    @GetMapping("/{employeeCode}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeByCode(@PathVariable String employeeCode) {
        LOGGER.info("GET /api/v1/employees/{} requested", employeeCode);
        EmployeeResponseDto employee = employeeService.getEmployeeByCode(employeeCode);
        return ResponseEntity.ok(employee);
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
     * API to check if an email already exists in the system.
     * Useful for dynamic client-side and external validation.
     *
     * @param email email to verify
     * @param excludeCode optional employee code to exclude (for update flows)
     * @return response entity containing exists boolean and message
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(
            @RequestParam String email,
            @RequestParam(required = false) String excludeCode) {
        LOGGER.info("GET /api/v1/employees/check-email requested (email='{}', excludeCode='{}')", email, excludeCode);
        boolean exists = employeeService.isEmailExists(email, excludeCode);
        return ResponseEntity.ok(java.util.Map.of(
                "email", email,
                "exists", exists,
                "message", exists ? "Email đã tồn tại trong hệ thống" : "Email hợp lệ"
        ));
    }

    /**
     * API to create a new employee persisted to the database with Bean Validation.
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
     * API demonstrating explicit @Valid and BindingResult validation handling (Module 5).
     *
     * @param requestDto validated request body
     * @param bindingResult binding result containing validation errors if any
     * @return response entity with created employee or 400 Bad Request with field errors
     */
    @PostMapping("/validate-demo")
    public ResponseEntity<?> createEmployeeWithBindingResult(
            @Valid @RequestBody EmployeeRequestDto requestDto,
            BindingResult bindingResult) {
        LOGGER.info("POST /api/v1/employees/validate-demo requested");
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }
        EmployeeResponseDto created = employeeService.createEmployee(requestDto);
        return ResponseEntity.ok(created);
    }

    /**
     * API to update an existing employee by employee code.
     *
     * @param employeeCode unique employee code
     * @param requestDto updated employee information
     * @return response entity with updated employee
     */
    @PutMapping("/{employeeCode}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(
            @PathVariable String employeeCode,
            @Valid @RequestBody EmployeeRequestDto requestDto) {
        LOGGER.info("PUT /api/v1/employees/{} requested", employeeCode);
        EmployeeResponseDto updated = employeeService.updateEmployee(employeeCode, requestDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * API to delete an employee by employee code.
     *
     * @param employeeCode unique employee code
     * @return 204 NO CONTENT on success
     */
    @DeleteMapping("/{employeeCode}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String employeeCode) {
        LOGGER.info("DELETE /api/v1/employees/{} requested", employeeCode);
        employeeService.deleteEmployee(employeeCode);
        return ResponseEntity.noContent().build();
    }
}
