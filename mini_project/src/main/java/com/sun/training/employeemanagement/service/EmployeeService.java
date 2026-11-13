package com.sun.training.employeemanagement.service;

import com.sun.training.employeemanagement.dto.EmployeeRegistrationDto;
import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.entity.Department;
import com.sun.training.employeemanagement.entity.Employee;
import com.sun.training.employeemanagement.exception.DuplicateResourceException;
import com.sun.training.employeemanagement.exception.ResourceNotFoundException;
import com.sun.training.employeemanagement.repository.DepartmentRepository;
import com.sun.training.employeemanagement.repository.EmployeeRepository;
import java.util.Comparator;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing employee operations with Spring Data JPA database persistence and exception handling.
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
                .sorted(Comparator.comparing(EmployeeResponseDto::getEmployeeCode))
                .toList();
    }

    /**
     * Retrieves an employee by employee code (e.g., N0001).
     *
     * @param employeeCode unique employee code
     * @return EmployeeResponseDto
     * @throws ResourceNotFoundException if no employee is found with given code
     */
    public EmployeeResponseDto getEmployeeByCode(String employeeCode) {
        LOGGER.info("Finding employee by code: {}", employeeCode);
        return employeeRepository.findByEmployeeCode(employeeCode.trim())
                .map(this::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Employee with code '%s' not found", employeeCode)));
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
                .sorted(Comparator.comparing(EmployeeResponseDto::getEmployeeCode))
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
                .sorted(Comparator.comparing(EmployeeResponseDto::getEmployeeCode))
                .toList();
    }

    /**
     * Retrieves the total count of employees in the system with 1-minute caching (Module 8).
     * Cached with key 'totalCount' under cache name 'employeeTotalCount'.
     *
     * @return total number of employees
     */
    @Cacheable(value = "employeeTotalCount", key = "'totalCount'")
    public long getTotalEmployeeCount() {
        LOGGER.info("Executing database query to count total employees (CACHE MISS)...");
        long count = employeeRepository.count();
        LOGGER.info("Total employee count retrieved from DB: {}", count);
        return count;
    }

    /**
     * Checks if an email already exists in the system, optionally excluding an employee code.
     *
     * @param email email to check
     * @param excludeCode optional employee code to exclude (for update flows)
     * @return true if email exists for another employee, false otherwise
     */
    public boolean isEmailExists(String email, String excludeCode) {
        if (email == null || email.isBlank()) {
            return false;
        }
        String cleanEmail = email.trim();
        return employeeRepository.findByEmail(cleanEmail)
                .map(emp -> excludeCode == null || excludeCode.isBlank()
                        || !emp.getEmployeeCode().equalsIgnoreCase(excludeCode.trim()))
                .orElseGet(() -> employeeRepository.existsByEmail(cleanEmail));
    }

    /**
     * Creates and persists a new employee in the database.
     *
     * @param requestDto request payload containing new employee information
     * @return newly created employee response DTO
     * @throws DuplicateResourceException if an employee with the same email already exists
     */
    @Transactional
    @CacheEvict(value = "employeeTotalCount", allEntries = true)
    public EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto) {
        LOGGER.info("Starting employee creation process for email: '{}', name: '{}'", requestDto.getEmail(), requestDto.getFullName());

        if (employeeRepository.existsByEmail(requestDto.getEmail())) {
            LOGGER.warn("Failed to create employee: Email '{}' already exists in the system", requestDto.getEmail());
            throw new DuplicateResourceException(
                    String.format("Employee with email '%s' already exists", requestDto.getEmail()));
        }

        String formattedName = utilityService.formatFullName(requestDto.getFullName());
        String code = generateUniqueEmployeeCode();
        Department department = getOrCreateDepartment(requestDto.getDepartment());

        LOGGER.debug("Resolved department: '{}' and assigned code: '{}' for new employee", department.getName(), code);

        Employee employee = new Employee(code, formattedName, requestDto.getEmail(), requestDto.getSalary(), department);
        Employee saved = employeeRepository.save(employee);
        LOGGER.info("Employee created successfully: [code='{}', name='{}', email='{}', department='{}', salary={}]",
                saved.getEmployeeCode(), saved.getName(), saved.getEmail(),
                saved.getDepartment() != null ? saved.getDepartment().getName() : "N/A", saved.getSalary());

        return toResponseDto(saved);
    }

    /**
     * Updates an existing employee in the database.
     *
     * @param employeeCode code of employee to update
     * @param requestDto updated employee details
     * @return updated EmployeeResponseDto
     * @throws ResourceNotFoundException if no employee is found with given code
     * @throws DuplicateResourceException if updated email already belongs to another employee
     */
    @Transactional
    public EmployeeResponseDto updateEmployee(String employeeCode, EmployeeRequestDto requestDto) {
        LOGGER.info("Starting employee update process for code: '{}'", employeeCode);

        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> {
                    LOGGER.warn("Failed to update employee: Employee with code '{}' not found", employeeCode);
                    return new ResourceNotFoundException(
                            String.format("Employee with code '%s' not found", employeeCode));
                });

        // Check email uniqueness if email is changed
        if (!employee.getEmail().equalsIgnoreCase(requestDto.getEmail())
                && employeeRepository.existsByEmail(requestDto.getEmail())) {
            LOGGER.warn("Failed to update employee '{}': Email '{}' is already in use by another employee",
                    employeeCode, requestDto.getEmail());
            throw new DuplicateResourceException(
                    String.format("Email '%s' is already in use by another employee", requestDto.getEmail()));
        }

        String formattedName = utilityService.formatFullName(requestDto.getFullName());
        Department department = getOrCreateDepartment(requestDto.getDepartment());

        employee.setName(formattedName);
        employee.setEmail(requestDto.getEmail());
        employee.setSalary(requestDto.getSalary());
        employee.setDepartment(department);

        Employee updated = employeeRepository.save(employee);
        LOGGER.info("Employee updated successfully: [code='{}', name='{}', email='{}', department='{}', salary={}]",
                updated.getEmployeeCode(), updated.getName(), updated.getEmail(),
                updated.getDepartment() != null ? updated.getDepartment().getName() : "N/A", updated.getSalary());

        return toResponseDto(updated);
    }

    /**
     * Deletes an employee by employee code.
     *
     * @param employeeCode unique employee code
     * @throws ResourceNotFoundException if employee with given code is not found
     */
    @Transactional
    @CacheEvict(value = "employeeTotalCount", allEntries = true)
    public void deleteEmployee(String employeeCode) {
        LOGGER.info("Starting employee deletion process for code: '{}'", employeeCode);

        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> {
                    LOGGER.warn("Failed to delete employee: Employee with code '{}' not found", employeeCode);
                    return new ResourceNotFoundException(
                            String.format("Employee with code '%s' not found", employeeCode));
                });

        employeeRepository.delete(employee);
        LOGGER.info("Employee with code '{}' deleted successfully from database.", employeeCode);
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
