package com.example.insurance_system.repository;

import com.example.insurance_system.model.Poliza;
import com.example.insurance_system.model.enums.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {
    List<Poliza> findByClienteId(Long clienteId);

    boolean existsByClienteIdAndTipoPoliza(Long clienteId, TipoPoliza tipoPoliza);
}
