package com.sun.training.employeemanagement.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration defining custom beans for Spring IoC Container.
 */
@Configuration
public class AppConfig {

    /**
     * Custom bean definition for ModelMapper.
     *
     * @return configured ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

