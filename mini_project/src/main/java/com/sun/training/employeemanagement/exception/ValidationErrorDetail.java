package com.sun.training.employeemanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Details of an individual field validation failure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorDetail {

    private String field;
    private Object rejectedValue;
    private String message;
}

