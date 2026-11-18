package com.sun.training.employeemanagement.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing overall employee and department statistics report (Module 10).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatisticsDto {

    private long totalEmployees;
    private long totalDepartments;
    private Double averageSalary;
    private Double highestSalary;
    private Double lowestSalary;
    private List<DepartmentStatisticDto> byDepartment;
}
