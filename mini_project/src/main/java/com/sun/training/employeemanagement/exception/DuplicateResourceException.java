package com.sun.training.employeemanagement.exception;

/**
 * Exception thrown when attempting to create or update a resource with duplicate unique fields.
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a new DuplicateResourceException with the specified detail message.
     *
     * @param message detail error message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}

