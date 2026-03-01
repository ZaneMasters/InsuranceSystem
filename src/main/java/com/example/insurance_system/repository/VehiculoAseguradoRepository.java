package com.example.insurance_system.repository;

import com.example.insurance_system.model.VehiculoAsegurado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad VehiculoAsegurado.
 * Maneja la persistencia y consulta de vehículos protegidos por una póliza.
 */
@Repository
public interface VehiculoAseguradoRepository extends JpaRepository<VehiculoAsegurado, Long> {

    /**
     * Retorna una lista con todos los vehículos amparados por una misma póliza.
     *
     * @param polizaId El ID de la póliza de donde se desean consultar los
     *                 vehículos.
     * @return Lista de vehículos asegurados en esa póliza.
     */
    List<VehiculoAsegurado> findByPolizaId(Long polizaId);
}
