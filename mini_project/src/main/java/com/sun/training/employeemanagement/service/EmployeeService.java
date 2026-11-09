package com.sun.training.employeemanagement.service;

import com.sun.training.employeemanagement.dto.EmployeeRegistrationDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service managing employee operations and demonstrating IoC / DI injection.
 */
@Service
public class EmployeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    private final UtilityService utilityService;
    private final ModelMapper modelMapper;

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
     * Prepares and registers an employee by mapping properties and enriching with utility functions.
     *
     * @param registrationDto input registration payload
     * @return employee response with formatted name and generated employee code
     */
    public EmployeeResponseDto registerEmployee(EmployeeRegistrationDto registrationDto) {
        LOGGER.info("Processing employee registration for name: {}", registrationDto.getFullName());

        EmployeeResponseDto responseDto = modelMapper.map(registrationDto, EmployeeResponseDto.class);
        responseDto.setFullName(utilityService.formatFullName(registrationDto.getFullName()));
        responseDto.setEmployeeCode(utilityService.generateEmployeeCode());

        LOGGER.info("Employee successfully processed with code: {}", responseDto.getEmployeeCode());
        return responseDto;
    }
}

