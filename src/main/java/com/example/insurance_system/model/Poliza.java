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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_poliza", nullable = false, unique = true, length = 50)
    private String numeroPoliza;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_poliza", nullable = false)
    private TipoPoliza tipoPoliza;

    @Enumerated(EnumType.STRING)
    @Column(name = "cobertura_salud")
    private CoberturaSalud coberturaSalud;

    @Column(name = "valor_asegurado", nullable = false)
    private BigDecimal valorAsegurado;

    @Column(name = "tarifa_base", nullable = false)
    private BigDecimal tarifaBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Beneficiario> beneficiarios = new ArrayList<>();

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VehiculoAsegurado> vehiculos = new ArrayList<>();

    /**
     * Helper para agregar un beneficiario y sincronizar la doble vía.
     */
    public void addBeneficiario(Beneficiario beneficiario) {
        beneficiarios.add(beneficiario);
        beneficiario.setPoliza(this);
    }

    /**
     * Helper para agregar un vehículo y sincronizar la doble vía.
     */
    public void addVehiculo(VehiculoAsegurado vehiculo) {
        vehiculos.add(vehiculo);
        vehiculo.setPoliza(this);
    }
}
