package com.example.insurance_system.model.enums;

/**
 * Enumeración que define los diferentes tipos de cobertura para pólizas de
 * Salud.
 */
public enum CoberturaSalud {
    /** Cobertura exclusiva para el cliente titular. */
    SOLO_CLIENTE,

    /** Cobertura para el cliente y sus padres. */
    CLIENTE_Y_PADRES,

    /** Cobertura familiar para el cliente, cónyuge e hijos. */
    CLIENTE_ESPOSA_E_HIJOS
}
