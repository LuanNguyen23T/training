package com.sun.training.employeemanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UtilityServiceTest {

    private UtilityService utilityService;

    @BeforeEach
    void setUp() {
        utilityService = new UtilityService();
    }

    @Test
    void testFormatFullName_withIrregularSpacesAndCase() {
        String result = utilityService.formatFullName("   nguyen   van  a  ");
        assertEquals("Nguyen Van A", result);
    }

    @Test
    void testFormatFullName_withMixedCase() {
        String result = utilityService.formatFullName("jOhN dOe");
        assertEquals("John Doe", result);
    }

    @Test
    void testFormatFullName_withNullOrBlank() {
        assertEquals("", utilityService.formatFullName(null));
        assertEquals("", utilityService.formatFullName("   "));
    }

    @Test
    void testGenerateEmployeeCode_sequentialGeneration() {
        String code1 = utilityService.generateEmployeeCode();
        String code2 = utilityService.generateEmployeeCode();

        assertNotNull(code1);
        assertNotNull(code2);
        assertTrue(code1.startsWith("N"));
        assertTrue(code2.startsWith("N"));
        assertEquals("N0001", code1);
        assertEquals("N0002", code2);
    }
}

