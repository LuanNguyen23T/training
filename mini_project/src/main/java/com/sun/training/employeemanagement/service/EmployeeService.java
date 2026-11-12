package com.sun.training.employeemanagement.service;

import com.sun.training.employeemanagement.dto.EmployeeRegistrationDto;
import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.entity.Department;
import com.sun.training.employeemanagement.entity.Employee;
import com.sun.training.employeemanagement.repository.DepartmentRepository;
import com.sun.training.employeemanagement.repository.EmployeeRepository;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing employee operations with Spring Data JPA database persistence.
 */
@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UtilityService utilityService;
    private final ModelMapper modelMapper;

    /**
     * Constructor injection for repositories and utility services.
     *
     * @param employeeRepository Spring Data JPA employee repository
     * @param departmentRepository Spring Data JPA department repository
     * @param utilityService custom UtilityService bean
     * @param modelMapper custom ModelMapper bean
     */
    public EmployeeService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            UtilityService utilityService,
            ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.utilityService = utilityService;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves all employees with optional filtering by department name or search keyword across fields.
     *
     * @param department optional department name filter
     * @param keyword optional search keyword across name, email, or employeeCode
     * @return list of matching employees mapped to response DTOs
     */
    public List<EmployeeResponseDto> getAllEmployees(String department, String keyword) {
        LOGGER.info("Fetching employees with filter - department: '{}', keyword: '{}'", department, keyword);
        return employeeRepository.searchEmployees(department, keyword)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Retrieves an employee by employee code (e.g., N0001).
     *
     * @param employeeCode unique employee code
     * @return optional containing EmployeeResponseDto if found
     */
    public Optional<EmployeeResponseDto> getEmployeeByCode(String employeeCode) {
        LOGGER.info("Finding employee by code: {}", employeeCode);
        if (employeeCode == null || employeeCode.isBlank()) {
            return Optional.empty();
        }
        return employeeRepository.findByEmployeeCode(employeeCode.trim())
                .map(this::toResponseDto);
    }

    /**
     * Searches employees by name containing keyword.
     *
     * @param name keyword to search in employee name
     * @return list of matching employees
     */
    public List<EmployeeResponseDto> searchEmployeesByName(String name) {
        LOGGER.info("Searching employees by name containing: '{}'", name);
        if (name == null || name.isBlank()) {
            return getAllEmployees(null, null);
        }
        return employeeRepository.findByNameContainingIgnoreCase(name.trim())
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Searches employees by department name.
     *
     * @param departmentName name of the department
     * @return list of matching employees
     */
    public List<EmployeeResponseDto> searchEmployeesByDepartment(String departmentName) {
        LOGGER.info("Searching employees by department: '{}'", departmentName);
        if (departmentName == null || departmentName.isBlank()) {
            return getAllEmployees(null, null);
        }
        return employeeRepository.findByDepartmentNameIgnoreCase(departmentName.trim())
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Creates and persists a new employee in the database.
     *
     * @param requestDto request payload containing new employee information
     * @return newly created employee response DTO
     */
    @Transactional
    public EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto) {
        LOGGER.info("Creating new employee with email: {}", requestDto.getEmail());

        if (employeeRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + requestDto.getEmail());
        }

        String formattedName = utilityService.formatFullName(requestDto.getFullName());
        String code = generateUniqueEmployeeCode();
        Department department = getOrCreateDepartment(requestDto.getDepartment());

        Employee employee = new Employee(code, formattedName, requestDto.getEmail(), requestDto.getSalary(), department);
        Employee saved = employeeRepository.save(employee);
        LOGGER.info("Saved employee [code={}, name={}] to database.", saved.getEmployeeCode(), saved.getName());

        return toResponseDto(saved);
    }

    /**
     * Updates an existing employee in the database.
     *
     * @param employeeCode code of employee to update
     * @param requestDto updated employee details
     * @return optional containing updated EmployeeResponseDto, or empty if not found
     */
    @Transactional
    public Optional<EmployeeResponseDto> updateEmployee(String employeeCode, EmployeeRequestDto requestDto) {
        LOGGER.info("Updating employee with code: {}", employeeCode);

        Optional<Employee> existingOpt = employeeRepository.findByEmployeeCode(employeeCode);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        Employee employee = existingOpt.get();

        // Check email uniqueness if email is changed
        if (!employee.getEmail().equalsIgnoreCase(requestDto.getEmail())
                && employeeRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use by another employee: " + requestDto.getEmail());
        }

        String formattedName = utilityService.formatFullName(requestDto.getFullName());
        Department department = getOrCreateDepartment(requestDto.getDepartment());

        employee.setName(formattedName);
        employee.setEmail(requestDto.getEmail());
        employee.setSalary(requestDto.getSalary());
        employee.setDepartment(department);

        Employee updated = employeeRepository.save(employee);
        LOGGER.info("Updated employee [code={}, name={}] successfully.", updated.getEmployeeCode(), updated.getName());

        return Optional.of(toResponseDto(updated));
    }

    /**
     * Deletes an employee by employee code.
     *
     * @param employeeCode unique employee code
     * @return true if deleted, false if not found
     */
    @Transactional
    public boolean deleteEmployee(String employeeCode) {
        LOGGER.info("Deleting employee with code: {}", employeeCode);

        Optional<Employee> existingOpt = employeeRepository.findByEmployeeCode(employeeCode);
        if (existingOpt.isEmpty()) {
            LOGGER.warn("Employee with code: {} not found for deletion.", employeeCode);
            return false;
        }

        employeeRepository.delete(existingOpt.get());
        LOGGER.info("Employee with code: {} successfully deleted.", employeeCode);
        return true;
    }

    /**
     * Prepares and registers an employee by mapping properties and enriching with utility functions (Module 2).
     *
     * @param registrationDto input registration payload
     * @return employee response with formatted name and generated employee code
     */
    public EmployeeResponseDto registerEmployee(EmployeeRegistrationDto registrationDto) {
        LOGGER.info("Processing employee registration for name: {}", registrationDto.getFullName());

        EmployeeResponseDto responseDto = modelMapper.map(registrationDto, EmployeeResponseDto.class);
        String code = utilityService.generateEmployeeCode();
        responseDto.setEmployeeCode(code);
        responseDto.setFullName(utilityService.formatFullName(registrationDto.getFullName()));

        LOGGER.info("Employee successfully processed with code: {}", responseDto.getEmployeeCode());
        return responseDto;
    }

    /**
     * Converts an Employee entity to EmployeeResponseDto.
     *
     * @param employee employee entity
     * @return mapped EmployeeResponseDto
     */
    public EmployeeResponseDto toResponseDto(Employee employee) {
        if (employee == null) {
            return null;
        }
        String deptName = employee.getDepartment() != null ? employee.getDepartment().getName() : null;
        return new EmployeeResponseDto(
                employee.getEmployeeCode(),
                employee.getName(),
                employee.getEmail(),
                deptName,
                employee.getSalary()
        );
    }

    private Department getOrCreateDepartment(String departmentName) {
        String trimmed = departmentName != null ? departmentName.trim() : "General";
        return departmentRepository.findByNameIgnoreCase(trimmed)
                .orElseGet(() -> {
                    LOGGER.info("Department '{}' not found. Creating new department...", trimmed);
                    Department newDept = new Department(trimmed, "Auto-created department");
                    return departmentRepository.save(newDept);
                });
    }

    private String generateUniqueEmployeeCode() {
        String code;
        do {
            code = utilityService.generateEmployeeCode();
        } while (employeeRepository.existsByEmployeeCode(code));
        return code;
    }
}
