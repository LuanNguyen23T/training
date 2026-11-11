package com.sun.training.employeemanagement.service;

import com.sun.training.employeemanagement.dto.EmployeeRegistrationDto;
import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.model.Employee;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service managing employee operations and demonstrating IoC / DI injection with in-memory persistence.
 */
@Service
public class EmployeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    private final UtilityService utilityService;
    private final ModelMapper modelMapper;

    private final List<Employee> inMemoryEmployees = new CopyOnWriteArrayList<>();

    /**
     * Constructor injection for required dependencies.
     *
     * @param utilityService custom @Service bean
     * @param modelMapper custom @Bean defined in @Configuration
     */
    public EmployeeService(UtilityService utilityService, ModelMapper modelMapper) {
        this.utilityService = utilityService;
        this.modelMapper = modelMapper;
    }

    /**
     * Pre-populates sample in-memory employee data for testing in Module 3.
     */
    @PostConstruct
    public void initSampleData() {
        LOGGER.info("Initializing sample in-memory employee data...");
        createEmployeeInternal("Nguyen Huu Hoang Luan", "luan.nguyen@sun-asterisk.com", "Engineering", 15000000.0);
        createEmployeeInternal("Tran Thi Binh", "binh.tran@sun-asterisk.com", "Human Resources", 12000000.0);
        createEmployeeInternal("Le Van Cuong", "cuong.le@sun-asterisk.com", "Finance", 14000000.0);
        LOGGER.info("Sample in-memory employee data initialized with {} records.", inMemoryEmployees.size());
    }

    /**
     * Retrieves all employees with optional filtering by department or search keyword.
     *
     * @param department optional department name filter
     * @param keyword optional search keyword across name, email, or employeeCode
     * @return list of matching employees mapped to response DTOs
     */
    public List<EmployeeResponseDto> getAllEmployees(String department, String keyword) {
        LOGGER.info("Fetching employees with filter - department: '{}', keyword: '{}'", department, keyword);

        return inMemoryEmployees.stream()
                .filter(emp -> matchesDepartment(emp, department))
                .filter(emp -> matchesKeyword(emp, keyword))
                .map(emp -> modelMapper.map(emp, EmployeeResponseDto.class))
                .toList();
    }

    /**
     * Retrieves an employee by employee code (e.g. N0001).
     *
     * @param employeeCode unique employee code
     * @return optional containing EmployeeResponseDto if found
     */
    public Optional<EmployeeResponseDto> getEmployeeByCode(String employeeCode) {
        LOGGER.info("Finding employee by code: {}", employeeCode);
        if (employeeCode == null || employeeCode.isBlank()) {
            return Optional.empty();
        }
        return inMemoryEmployees.stream()
                .filter(emp -> emp.getEmployeeCode() != null
                        && emp.getEmployeeCode().equalsIgnoreCase(employeeCode.trim()))
                .findFirst()
                .map(emp -> modelMapper.map(emp, EmployeeResponseDto.class));
    }

    /**
     * Creates and stores a new employee in-memory.
     *
     * @param requestDto request payload containing new employee information
     * @return newly created employee response DTO
     */
    public EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto) {
        LOGGER.info("Creating new employee with email: {}", requestDto.getEmail());

        Employee created = createEmployeeInternal(
                requestDto.getFullName(),
                requestDto.getEmail(),
                requestDto.getDepartment(),
                requestDto.getSalary()
        );

        return modelMapper.map(created, EmployeeResponseDto.class);
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

    private Employee createEmployeeInternal(String fullName, String email, String department, Double salary) {
        String code = utilityService.generateEmployeeCode();
        String formattedName = utilityService.formatFullName(fullName);

        Employee employee = new Employee(code, formattedName, email, department, salary);
        inMemoryEmployees.add(employee);
        LOGGER.info("Added employee [code={}, name={}] to in-memory store.", code, formattedName);
        return employee;
    }

    private boolean matchesDepartment(Employee emp, String department) {
        if (department == null || department.isBlank()) {
            return true;
        }
        return emp.getDepartment() != null
                && emp.getDepartment().toLowerCase(Locale.ROOT).contains(department.trim().toLowerCase(Locale.ROOT));
    }

    private boolean matchesKeyword(Employee emp, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String lowerKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        return (emp.getFullName() != null && emp.getFullName().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                || (emp.getEmail() != null && emp.getEmail().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                || (emp.getEmployeeCode() != null && emp.getEmployeeCode().toLowerCase(Locale.ROOT).contains(lowerKeyword));
    }
}
