package com.example.insurance_system.repository;

import com.example.insurance_system.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Beneficiario.
 * Proporciona métodos para interactuar con la base de datos de beneficiarios.
 */
@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

    /**
     * Busca y retorna todos los beneficiarios asociados a un ID de póliza
     * específico.
     *
     * @param polizaId El identificador de la póliza relacionada.
     * @return Lista de beneficiarios de dicha póliza.
     */
    List<Beneficiario> findByPolizaId(Long polizaId);
}
