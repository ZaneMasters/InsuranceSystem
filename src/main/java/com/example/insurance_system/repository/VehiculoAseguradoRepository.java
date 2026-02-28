package com.example.insurance_system.repository;

import com.example.insurance_system.model.VehiculoAsegurado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoAseguradoRepository extends JpaRepository<VehiculoAsegurado, Long> {
    List<VehiculoAsegurado> findByPolizaId(Long polizaId);
}
