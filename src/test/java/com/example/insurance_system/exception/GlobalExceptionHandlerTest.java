package com.example.insurance_system.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleApplicationException_ShouldReturnExpectedError() {
        // Arrange
        String errorMessage = "Custom error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApplicationException ex = new ApplicationException(errorMessage, status);

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleApplicationException(ex);

        // Assert
        assertEquals(status, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().get("error"));
    }

    @Test
    void handleValidationExceptions_ShouldReturnFieldErrors() throws NoSuchMethodException {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodParameter methodParameter = new MethodParameter(this.getClass().getDeclaredMethod("setUp"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("must not be blank", response.getBody().get("fieldName"));
    }

    @Test
    void handleGeneralExceptions_ShouldReturnInternalServerError() {
        // Arrange
        String exceptionMessage = "Unexpected problem";
        Exception ex = new Exception(exceptionMessage);

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGeneralExceptions(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor: " + exceptionMessage, response.getBody().get("error"));
    }
}
