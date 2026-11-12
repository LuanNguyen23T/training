package com.sun.training.employeemanagement.controller;

import com.sun.training.employeemanagement.dto.DepartmentResponseDto;
import com.sun.training.employeemanagement.repository.DepartmentRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Department operations in Module 4.
 */
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentRepository departmentRepository;

    /**
     * Constructor injection of DepartmentRepository.
     *
     * @param departmentRepository department repository bean
     */
    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * API to retrieve all departments with employee counts.
     *
     * @return response entity with list of departments
     */
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>> getAllDepartments() {
        LOGGER.info("GET /api/v1/departments requested");
        List<DepartmentResponseDto> departments = departmentRepository.findAllWithEmployees().stream()
                .map(d -> new DepartmentResponseDto(
                        d.getId(),
                        d.getName(),
                        d.getDescription(),
                        d.getEmployees() != null ? d.getEmployees().size() : 0))
                .toList();
        return ResponseEntity.ok(departments);
    }
}
