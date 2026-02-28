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

@RestController
@RequestMapping("/api/v1/polizas")
@RequiredArgsConstructor
@Tag(name = "Pólizas", description = "API para la gestión de pólizas de seguros")
public class PolizaController {

    private final PolizaService polizaService;

    @PostMapping
    @Operation(summary = "Crear Póliza", description = "Crea una póliza aplicando reglas de Vida, Vehículo o Salud")
    public ResponseEntity<PolizaDTO> crearPoliza(@Valid @RequestBody PolizaDTO dto) {
        return new ResponseEntity<>(polizaService.crearPoliza(dto), HttpStatus.CREATED);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar pólizas por cliente", description = "Obtiene todas las pólizas asociadas a un cliente")
    public ResponseEntity<List<PolizaDTO>> listarPolizasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(polizaService.listarPolizasPorCliente(clienteId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar detalle de póliza", description = "Obtiene los detalles de una póliza incluyendo asegurados o beneficiarios")
    public ResponseEntity<PolizaDTO> consultarDetallePoliza(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.consultarDetallePoliza(id));
    }

    @GetMapping("/{id}/beneficiarios")
    @Operation(summary = "Listar beneficiarios de póliza", description = "Lista los beneficiarios de una póliza de vida o salud")
    public ResponseEntity<List<BeneficiarioDTO>> listarBeneficiariosSalud(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.listarBeneficiariosPorPoliza(id));
    }
}
