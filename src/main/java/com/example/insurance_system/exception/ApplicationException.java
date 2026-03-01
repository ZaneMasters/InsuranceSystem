package com.example.insurance_system.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada manejada a lo largo de la aplicación.
 * Permite arrojar un error de negocio que contiene un código de estado HTTP
 * específico.
 */
public class ApplicationException extends RuntimeException {

    /**
     * Estado HTTP que será retornado al cliente al lanzarse esta excepción.
     */
    private final HttpStatus status;

    /**
     * Construye una nueva excepción de aplicación.
     *
     * @param message El mensaje descriptivo del error.
     * @param status  El código de estado HTTP apropiado (ej: 400 Bad Request, 404
     *                Not Found).
     */
    public ApplicationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Obtiene el estado HTTP vinculado al error.
     *
     * @return El objeto HttpStatus.
     */
    public HttpStatus getStatus() {
        return status;
    }
}
