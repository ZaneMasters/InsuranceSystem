package com.example.insurance_system.model;

import com.example.insurance_system.model.enums.Parentesco;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa al Beneficiario de una póliza (Vida o Salud).
 */
@Entity
@Table(name = "beneficiarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficiario {

    /**
     * Identificador único del beneficiario en base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombres del beneficiario.
     */
    @Column(nullable = false, length = 100)
    private String nombres;

    /**
     * Apellidos del beneficiario.
     */
    @Column(nullable = false, length = 100)
    private String apellidos;

    /**
     * Tipo de documento de identidad del beneficiario.
     */
    @Column(name = "tipo_documento", length = 10)
    private String tipoDocumento;

    /**
     * Número de documento de identidad del beneficiario.
     */
    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    /**
     * Parentesco o relación familiar del beneficiario con el cliente titular.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Parentesco parentesco;

    /**
     * Referencia a la póliza en la que está inscrito el beneficiario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;
}
