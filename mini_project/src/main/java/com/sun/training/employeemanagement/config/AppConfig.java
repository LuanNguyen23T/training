package com.sun.training.employeemanagement.config;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.sun.training.employeemanagement.dto.DepartmentResponseDto;
import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.entity.Department;
import com.sun.training.employeemanagement.entity.Employee;

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

    /**
     * Whitelist application classes for Thymeleaf SpEL evaluation.
     * Classes under "com.sun.*" are restricted by Thymeleaf's default ACL security check
     * because "com.sun" is a reserved JDK internal prefix.
     *
     * @return BeanPostProcessor configuring allowed classes on SpringTemplateEngine
     */
    @Bean
    public static BeanPostProcessor thymeleafSecurityCustomizer() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof SpringTemplateEngine engine) {
                    engine.setAllowedClassOverridesForViews(Set.of(
                            EmployeeResponseDto.class,
                            EmployeeRequestDto.class,
                            DepartmentResponseDto.class,
                            Employee.class,
                            Department.class
                    ));
                }
                return bean;
            }
        };
    }
}


