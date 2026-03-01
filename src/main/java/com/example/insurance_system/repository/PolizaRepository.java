package com.example.insurance_system.repository;

import com.example.insurance_system.model.Poliza;
import com.example.insurance_system.model.enums.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Poliza.
 * Contiene operaciones para la consulta y almacenamiento de pólizas.
 */
@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    /**
     * Encuentra todas las pólizas pertenecientes a un determinado cliente.
     *
     * @param clienteId El ID del cliente.
     * @return Una lista de las pólizas asociadas a dicho cliente.
     */
    List<Poliza> findByClienteId(Long clienteId);

    /**
     * Verifica la existencia de una póliza para un cliente dado según un tipo
     * específico.
     * Utilizado para validar las reglas de negocio (ej. clientes solo pueden tener
     * 1 póliza de VIDA).
     *
     * @param clienteId  El identificador del cliente.
     * @param tipoPoliza El tipo de seguro a consultar (VIDA, VEHICULO, SALUD).
     * @return True si ya existe una póliza de ese tipo para el cliente, False en
     *         caso contrario.
     */
    boolean existsByClienteIdAndTipoPoliza(Long clienteId, TipoPoliza tipoPoliza);
}
