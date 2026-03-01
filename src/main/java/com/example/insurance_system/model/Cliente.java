package com.example.insurance_system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un cliente cotizante de la aseguradora.
 */
@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    /**
     * Identificador único del cliente (Generado automáticamente).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo de documento de identidad (ej. CC, CE, Pasaporte).
     */
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;

    /**
     * Número del documento de identidad. Debe ser único.
     */
    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    /**
     * Nombres del cliente.
     */
    @Column(nullable = false, length = 100)
    private String nombres;

    /**
     * Apellidos del cliente.
     */
    @Column(nullable = false, length = 100)
    private String apellidos;

    /**
     * Correo electrónico de contacto. Debe ser único.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Número de teléfono de contacto.
     */
    @Column(length = 20)
    private String telefono;

    /**
     * Fecha de nacimiento del cliente.
     */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Lista de pólizas asociadas al cliente.
     */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Poliza> polizas = new ArrayList<>();
}
