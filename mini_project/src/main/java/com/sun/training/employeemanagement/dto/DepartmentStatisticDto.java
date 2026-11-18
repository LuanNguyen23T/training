package com.sun.training.employeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing statistics for a specific department (Module 10).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentStatisticDto {

    private String departmentName;
    private long employeeCount;
    private Double averageSalary;
    private Double percentage;

    /**
     * Constructor for JPQL constructor expression projection with 2 arguments.
     *
     * @param departmentName name of the department
     * @param employeeCount number of employees in the department
     */
    public DepartmentStatisticDto(String departmentName, long employeeCount) {
        this.departmentName = departmentName;
        this.employeeCount = employeeCount;
        this.averageSalary = 0.0;
        this.percentage = 0.0;
    }

    /**
     * Constructor for JPQL constructor expression projection with 3 arguments.
     *
     * @param departmentName name of the department
     * @param employeeCount number of employees in the department
     * @param averageSalary average salary of employees in the department
     */
    public DepartmentStatisticDto(String departmentName, long employeeCount, Double averageSalary) {
        this.departmentName = departmentName;
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary != null ? averageSalary : 0.0;
        this.percentage = 0.0;
    }
}
