package com.example.insurance_system.model;

import com.example.insurance_system.model.enums.CoberturaSalud;
import com.example.insurance_system.model.enums.TipoPoliza;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una Póliza de seguro (Vida, Vehículo o Salud).
 */
@Entity
@Table(name = "polizas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Poliza {

    /**
     * Identificador único de la póliza (Autogenerado).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número único asignado comercialmente a la póliza.
     */
    @Column(name = "numero_poliza", nullable = false, unique = true, length = 50)
    private String numeroPoliza;

    /**
     * Fecha en la que inicia la vigencia de la póliza.
     */
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    /**
     * Fecha en la que finaliza la vigencia de la póliza.
     */
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    /**
     * Tipo de seguro que ampara la póliza (VIDA, VEHICULO, SALUD).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_poliza", nullable = false)
    private TipoPoliza tipoPoliza;

    /**
     * Nivel de cobertura de salud, aplicable solo si el tipo de póliza es SALUD.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "cobertura_salud")
    private CoberturaSalud coberturaSalud;

    /**
     * Monto total asegurado dentro del contrato.
     */
    @Column(name = "valor_asegurado", nullable = false)
    private BigDecimal valorAsegurado;

    /**
     * Costo base que el cliente pagará por la póliza.
     */
    @Column(name = "tarifa_base", nullable = false)
    private BigDecimal tarifaBase;

    /**
     * Relación con el cliente titular de la póliza.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /**
     * Lista de beneficiarios amparados en la póliza (Ej: Vida, Salud).
     */
    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Beneficiario> beneficiarios = new ArrayList<>();

    /**
     * Lista de vehículos amparados en la póliza (Ej: Vehículo).
     */
    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VehiculoAsegurado> vehiculos = new ArrayList<>();

    /**
     * Helper para agregar un beneficiario y sincronizar la doble vía.
     *
     * @param beneficiario Instancia a asociar con esta póliza.
     */
    public void addBeneficiario(Beneficiario beneficiario) {
        beneficiarios.add(beneficiario);
        beneficiario.setPoliza(this);
    }

    /**
     * Helper para agregar un vehículo y sincronizar la doble vía.
     *
     * @param vehiculo Instancia a asociar con esta póliza.
     */
    public void addVehiculo(VehiculoAsegurado vehiculo) {
        vehiculos.add(vehiculo);
        vehiculo.setPoliza(this);
    }
}
