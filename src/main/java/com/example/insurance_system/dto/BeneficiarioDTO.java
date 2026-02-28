package com.example.insurance_system.dto;

import com.example.insurance_system.model.enums.Parentesco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BeneficiarioDTO {
    private Long id;

    @NotBlank(message = "El nombre del beneficiario es obligatorio")
    private String nombres;

    @NotBlank(message = "El apellido del beneficiario es obligatorio")
    private String apellidos;

    private String tipoDocumento;

    private String numeroDocumento;

    @NotNull(message = "El parentesco es obligatorio")
    private Parentesco parentesco;
}
