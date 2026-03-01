package com.example.insurance_system.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un Vehículo Asegurado en una póliza de Vehículo.
 */
@Entity
@Table(name = "vehiculos_asegurados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculoAsegurado {

    /**
     * Identificador único del vehículo en el sistema.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Placa registrada del vehículo.
     */
    @Column(nullable = false, length = 20)
    private String placa;

    /**
     * Marca de la compañía fabricante del vehículo.
     */
    @Column(nullable = false, length = 50)
    private String marca;

    /**
     * Modelo o línea del vehículo.
     */
    @Column(nullable = false, length = 50)
    private String modelo;

    /**
     * Año de fabricación o modelo comercial del vehículo.
     */
    @Column(nullable = false)
    private Integer anio;

    /**
     * Póliza a la cual está ligado el aseguramiento de este vehículo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;
}
