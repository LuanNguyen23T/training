package com.sun.training.employeemanagement.config;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test verifying Profile activation and configuration resolution (Lab 7).
 */
@SpringBootTest
@ActiveProfiles("dev")
class ProfileConfigurationIntegrationTest {

    @Autowired
    private Environment environment;

    @Test
    @DisplayName("Should have 'dev' profile active in development test configuration")
    void testDevProfileActive() {
        assertArrayEquals(new String[]{"dev"}, environment.getActiveProfiles(),
                "Active profile should be 'dev'");
    }

    @Test
    @DisplayName("Should resolve dev profile database and h2 console properties")
    void testDevProfileDatabaseProperties() {
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        assertNotNull(datasourceUrl, "Datasource URL should be configured");
        assertTrue(datasourceUrl.contains("h2:file:"), "Dev datasource URL should use H2 file-based storage");

        String driverClass = environment.getProperty("spring.datasource.driver-class-name");
        assertEquals("org.h2.Driver", driverClass, "Dev driver class should be org.h2.Driver");

        String showSql = environment.getProperty("spring.jpa.show-sql");
        assertEquals("true", showSql, "Dev JPA show-sql should be enabled");

        String h2ConsoleEnabled = environment.getProperty("spring.h2.console.enabled");
        assertEquals("true", h2ConsoleEnabled, "H2 Console should be enabled in dev profile");
    }
}

