package com.sun.training.employeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object representing employee details response.
 * Uses employeeCode as the single identifier for compactness.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {

    private String employeeCode;
    private String fullName;
    private String email;
    private String department;
    private Double salary;

    /**
     * Backward-compatible constructor for Module 2.
     *
     * @param employeeCode unique employee code
     * @param fullName full name of employee
     * @param email employee email
     * @param department employee department
     */
    public EmployeeResponseDto(String employeeCode, String fullName, String email, String department) {
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
    }
}
