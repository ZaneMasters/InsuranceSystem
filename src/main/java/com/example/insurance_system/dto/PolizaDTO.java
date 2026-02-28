package com.example.insurance_system.dto;

import com.example.insurance_system.model.enums.CoberturaSalud;
import com.example.insurance_system.model.enums.TipoPoliza;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PolizaDTO {
    private Long id;

    @NotNull(message = "Debe enviar el ID del cliente")
    private Long clienteId;

    @NotNull(message = "El número de póliza es obligatorio")
    private String numeroPoliza;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "El tipo de póliza es obligatorio")
    private TipoPoliza tipoPoliza;

    private CoberturaSalud coberturaSalud;

    @NotNull(message = "El valor asegurado es obligatorio")
    private BigDecimal valorAsegurado;

    private BigDecimal tarifaBase;

    private List<BeneficiarioDTO> beneficiarios;

    private List<VehiculoAseguradoDTO> vehiculos;
}
