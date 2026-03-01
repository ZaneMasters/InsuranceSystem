package com.example.insurance_system.repository;

import com.example.insurance_system.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Cliente.
 * Proporciona los métodos para gestionar la persistencia de clientes en el
 * sistema.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca a un cliente mediante su número de documento de identidad.
     *
     * @param numeroDocumento El número de documento a buscar.
     * @return Una instancia de Optional que puede contener al cliente si es
     *         hallado.
     */
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
}
