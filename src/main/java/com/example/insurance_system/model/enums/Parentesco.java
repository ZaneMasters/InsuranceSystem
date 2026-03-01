package com.example.insurance_system.model.enums;

/**
 * Enumeración de los diferentes grados de parentesco permitidos para los
 * beneficiarios.
 */
public enum Parentesco {
    /** Padre del titular. */
    PADRE,

    /** Madre del titular. */
    MADRE,

    /** Cónyuge civil o esposo(a) del titular. */
    CONYUGE,

    /** Hijo o hija del titular. */
    HIJO,

    /** Otro parentesco no especificado. */
    OTRO
}
