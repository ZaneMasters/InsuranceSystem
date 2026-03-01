package com.example.insurance_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 * Captura las excepciones lanzadas por los controladores y servicios
 * para estandarizar el formato de respuesta de errores.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones de negocio de tipo ApplicationException.
     *
     * @param ex La excepción lanzada.
     * @return Respuesta HTTP con el estado previsto en la excepción y un JSON con
     *         el mensaje.
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, String>> handleApplicationException(ApplicationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    /**
     * Captura excepciones relacionadas con validaciones de campos (ej,
     * anotaciones @NotBlank o @NotNull).
     *
     * @param ex La excepción que contiene los errores de binding.
     * @return Respuesta HTTP 400 (Bad Request) detallando los errores por campo en
     *         un mapa JSON.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Intercepta todas las excepciones generales no manejadas de manera específica.
     *
     * @param ex Excepción subyacente.
     * @return Respuesta HTTP 500 (Internal Server Error) indicando una falla no
     *         controlada.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralExceptions(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Error interno del servidor: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
