package com.sun.training.employeemanagement.exception;

/**
 * Exception thrown when a requested resource is not found in the system.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message detail error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

