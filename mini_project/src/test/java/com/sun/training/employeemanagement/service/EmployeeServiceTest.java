package com.sun.training.employeemanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sun.training.employeemanagement.dto.EmployeeRegistrationDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private UtilityService utilityService;

    @Mock
    private ModelMapper modelMapper;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(utilityService, modelMapper);
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
        when(utilityService.generateEmployeeCode()).thenReturn("EMP-00001");

        EmployeeResponseDto result = employeeService.registerEmployee(request);

        assertNotNull(result);
        assertEquals("EMP-00001", result.getEmployeeCode());
        assertEquals("Nguyen Van A", result.getFullName());
        assertEquals("nguyenvana@sun-asterisk.com", result.getEmail());
        assertEquals("Engineering", result.getDepartment());

        verify(modelMapper).map(request, EmployeeResponseDto.class);
        verify(utilityService).formatFullName("  nguyen van a ");
        verify(utilityService).generateEmployeeCode();
    }
}

