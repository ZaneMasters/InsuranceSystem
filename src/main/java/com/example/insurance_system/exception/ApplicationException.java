package com.example.insurance_system.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception to throw generic application errors with a specific HTTP
 * status code.
 */
public class ApplicationException extends RuntimeException {

    private final HttpStatus status;

    public ApplicationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
