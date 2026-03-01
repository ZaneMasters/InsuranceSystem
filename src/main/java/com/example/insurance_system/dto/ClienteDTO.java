package com.example.insurance_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Objeto de Transferencia de Datos (DTO) para Cliente.
 * Contiene la información requerida para registrar o actualizar clientes.
 */
@Data
@Builder
public class ClienteDTO {

    /** Identificador único del cliente. */
    private Long id;

    /** Tipo de documento (CC, CE, etc.). */
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    /** Número de documento de identidad. */
    @NotBlank(message = "El número de documento es obligatorio")
    private String numeroDocumento;

    /** Nombres del cliente. */
    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    /** Apellidos del cliente. */
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    /** Correo electrónico de contacto del cliente. */
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    /** Teléfono de contacto del cliente. */
    private String telefono;

    /** Fecha de nacimiento del cliente. */
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;
}
