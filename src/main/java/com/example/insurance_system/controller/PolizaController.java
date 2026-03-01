package com.example.insurance_system.controller;

import com.example.insurance_system.dto.BeneficiarioDTO;
import com.example.insurance_system.dto.PolizaDTO;
import com.example.insurance_system.service.PolizaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las operaciones relacionadas con las pólizas.
 * Permite crear pólizas y realizar consultas de las mismas, así como de sus
 * beneficiarios.
 */
@RestController
@RequestMapping("/api/v1/polizas")
@RequiredArgsConstructor
@Tag(name = "Pólizas", description = "API para la gestión de pólizas de seguros")
public class PolizaController {

    /**
     * Servicio que contiene la lógica de negocio para las pólizas.
     */
    private final PolizaService polizaService;

    /**
     * Crea una nueva póliza asociada a un cliente, aplicando las reglas de negocio
     * pertinentes según el tipo de póliza (Vida, Vehículo o Salud).
     *
     * @param dto El objeto con los datos de la póliza a crear.
     * @return Una respuesta HTTP 201 (CREATED) con los datos de la póliza creada.
     */
    @PostMapping
    @Operation(summary = "Crear Póliza", description = "Crea una póliza aplicando reglas de Vida, Vehículo o Salud")
    public ResponseEntity<PolizaDTO> crearPoliza(@Valid @RequestBody PolizaDTO dto) {
        return new ResponseEntity<>(polizaService.crearPoliza(dto), HttpStatus.CREATED);
    }

    /**
     * Lista todas las pólizas asociadas a un cliente específico.
     *
     * @param clienteId El ID del cliente cuyas pólizas se desean consultar.
     * @return Una respuesta HTTP 200 (OK) con la lista de pólizas del cliente.
     */
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar pólizas por cliente", description = "Obtiene todas las pólizas asociadas a un cliente")
    public ResponseEntity<List<PolizaDTO>> listarPolizasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(polizaService.listarPolizasPorCliente(clienteId));
    }

    /**
     * Consulta los detalles de una póliza en particular utilizando su ID.
     *
     * @param id El identificador único de la póliza.
     * @return Una respuesta HTTP 200 (OK) con el detalle de la póliza, incluyendo
     *         beneficiarios o vehículos.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consultar detalle de póliza", description = "Obtiene los detalles de una póliza incluyendo asegurados o beneficiarios")
    public ResponseEntity<PolizaDTO> consultarDetallePoliza(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.consultarDetallePoliza(id));
    }

    /**
     * Obtiene una lista de los beneficiarios asociados a una póliza específica
     * (principalmente aplicable a pólizas de Vida o Salud).
     *
     * @param id El ID de la póliza.
     * @return Una respuesta HTTP 200 (OK) indicando los beneficiarios registrados.
     */
    @GetMapping("/{id}/beneficiarios")
    @Operation(summary = "Listar beneficiarios de póliza", description = "Lista los beneficiarios de una póliza de vida o salud")
    public ResponseEntity<List<BeneficiarioDTO>> listarBeneficiariosSalud(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.listarBeneficiariosPorPoliza(id));
    }
}
