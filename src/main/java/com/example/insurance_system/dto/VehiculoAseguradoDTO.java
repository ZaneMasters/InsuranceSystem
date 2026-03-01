package com.example.insurance_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Objeto de Transferencia de Datos (DTO) para VehiculoAsegurado.
 * Representa los datos introducidos para la creación o consulta
 * de los vehículos adjuntos a una póliza.
 */
@Data
@Builder
public class VehiculoAseguradoDTO {

    /** Identificador de la base de datos (si aplica). */
    private Long id;

    /** Número de placa del vehículo (Ej: ABC-123). */
    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    /** Marca comercial del vehículo. */
    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    /** Modelo específico del vehículo. */
    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    /** Año de manufactura del vehículo. */
    @NotNull(message = "El año es obligatorio")
    private Integer anio;
}
