package com.sun.training.employeemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Domain model representing an Employee for in-memory operations.
 * Uses employeeCode as the single primary identifier.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private String employeeCode;
    private String fullName;
    private String email;
    private String department;
    private Double salary;
}
