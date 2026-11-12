package com.sun.training.employeemanagement;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class EmployeeManagementApplicationTests {

    private final ApplicationContext applicationContext;

    @Autowired
    EmployeeManagementApplicationTests(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
    void testContextLoads() {
        assertNotNull(applicationContext, "Application context should load successfully");
    }
}
