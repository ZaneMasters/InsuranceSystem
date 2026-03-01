package com.example.insurance_system.dto;

import com.example.insurance_system.model.enums.CoberturaSalud;
import com.example.insurance_system.model.enums.TipoPoliza;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Objeto de Transferencia de Datos (DTO) para Poliza.
 * Se utiliza para orquestar la creación de pólizas (VIDA, SALUD, VEHICULO)
 * mediante peticiones al controlador REST.
 */
@Data
@Builder
public class PolizaDTO {

    /** Identificador único de la póliza (id). */
    private Long id;

    /** El ID del cliente titular. */
    @NotNull(message = "Debe enviar el ID del cliente")
    private Long clienteId;

    /** Número comercial de la póliza. */
    @NotNull(message = "El número de póliza es obligatorio")
    private String numeroPoliza;

    /** Fecha de inicio de vigencia. */
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    /** Fecha de fin de vigencia. */
    @NotNull(message = "La fecha fin es obligatoria")
    private LocalDate fechaFin;

    /** Tipo de póliza seleccionado. */
    @NotNull(message = "El tipo de póliza es obligatorio")
    private TipoPoliza tipoPoliza;

    /** Plan de cobertura, solo para póliza de SALUD. */
    private CoberturaSalud coberturaSalud;

    /** Monto del rubro asegurado en el contrato. */
    @NotNull(message = "El valor asegurado es obligatorio")
    private BigDecimal valorAsegurado;

    /** Costo actual que el cliente abonará por el seguro. */
    private BigDecimal tarifaBase;

    /** Colección de beneficiarios asociados, requeridos por Vida y Salud. */
    private List<BeneficiarioDTO> beneficiarios;

    /** Colección de vehículos asegurados, requeridos por Vehiculo. */
    private List<VehiculoAseguradoDTO> vehiculos;
}
