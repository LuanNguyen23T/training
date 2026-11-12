package com.sun.training.employeemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for Employee Management Spring Boot application.
 */
@SpringBootApplication
public class EmployeeManagementApplication {

    /**
     * Main method running Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }
}
