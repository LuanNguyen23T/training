package com.sun.training.employeemanagement.controller;

import com.sun.training.employeemanagement.dto.EmployeeRegistrationDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.service.EmployeeService;
import com.sun.training.employeemanagement.service.UtilityService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller demonstrating IoC and Dependency Injection in Module 2.
 */
@RestController
@RequestMapping("/api/v1/demo")
public class EmployeeDemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeDemoController.class);

    private final UtilityService utilityService;
    private final EmployeeService employeeService;

    /**
     * Constructor injection of services.
     *
     * @param utilityService utility service bean
     * @param employeeService employee service bean
     */
    public EmployeeDemoController(UtilityService utilityService, EmployeeService employeeService) {
        this.utilityService = utilityService;
        this.employeeService = employeeService;
    }

    /**
     * API to generate a new sequential employee code.
     *
     * @return generated employee code
     */
    @GetMapping("/generate-code")
    public ResponseEntity<String> generateEmployeeCode() {
        LOGGER.info("GET /api/v1/demo/generate-code requested");
        String code = utilityService.generateEmployeeCode();
        return ResponseEntity.ok(code);
    }

    /**
     * API to format a person's raw name into Title Case.
     *
     * @param name raw name query parameter
     * @return normalized name
     */
    @GetMapping("/format-name")
    public ResponseEntity<String> formatName(@RequestParam(defaultValue = "") String name) {
        LOGGER.info("GET /api/v1/demo/format-name requested for name: '{}'", name);
        String formatted = utilityService.formatFullName(name);
        return ResponseEntity.ok(formatted);
    }

    /**
     * API demonstrating registration mapping and enrichment via injected custom beans.
     *
     * @param registrationDto validated employee registration input
     * @return employee response with formatted name and generated employee code
     */
    @PostMapping("/register")
    public ResponseEntity<EmployeeResponseDto> register(
            @Valid @RequestBody EmployeeRegistrationDto registrationDto) {
        LOGGER.info("POST /api/v1/demo/register requested for email: {}", registrationDto.getEmail());
        EmployeeResponseDto response = employeeService.registerEmployee(registrationDto);
        return ResponseEntity.ok(response);
    }
}

