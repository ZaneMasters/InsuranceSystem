package com.example.insurance_system.dto;

import com.example.insurance_system.model.enums.Parentesco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Objeto de Transferencia de Datos (DTO) para Beneficiario.
 * Utilizado para enviar y recibir datos sobre beneficiarios en la API.
 */
@Data
@Builder
public class BeneficiarioDTO {

    /** Identificador único del beneficiario. */
    private Long id;

    /** Nombres del beneficiario. Es obligatorio. */
    @NotBlank(message = "El nombre del beneficiario es obligatorio")
    private String nombres;

    /** Apellidos del beneficiario. Es obligatorio. */
    @NotBlank(message = "El apellido del beneficiario es obligatorio")
    private String apellidos;

    /** Tipo de documento de identidad. */
    private String tipoDocumento;

    /** Número del documento de identidad. */
    private String numeroDocumento;

    /** Relación o parentesco con el titular de la póliza. */
    @NotNull(message = "El parentesco es obligatorio")
    private Parentesco parentesco;
}
