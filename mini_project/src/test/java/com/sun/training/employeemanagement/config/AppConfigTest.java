package com.sun.training.employeemanagement.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class AppConfigTest {

    @Test
    void testModelMapperBeanCreation() {
        AppConfig config = new AppConfig();
        ModelMapper mapper = config.modelMapper();
        assertNotNull(mapper, "ModelMapper bean should be initialized");
    }
}

