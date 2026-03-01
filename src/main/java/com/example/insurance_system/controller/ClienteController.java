package com.example.insurance_system.controller;

import com.example.insurance_system.dto.ClienteDTO;
import com.example.insurance_system.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los
 * clientes.
 * Proporciona endpoints para crear, consultar, actualizar y eliminar clientes.
 */
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API para la gestión de clientes")
public class ClienteController {

    /**
     * Servicio de lógica de negocio para los clientes.
     */
    private final ClienteService clienteService;

    /**
     * Crea un nuevo cliente en el sistema.
     *
     * @param dto El objeto de transferencia de datos con la información del
     *            cliente.
     * @return Una respuesta HTTP 201 (CREATED) con los datos del cliente creado.
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo cliente", description = "Registra un cliente en el sistema")
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO dto) {
        return new ResponseEntity<>(clienteService.crearCliente(dto), HttpStatus.CREATED);
    }

    /**
     * Obtiene los detalles de un cliente específico por su ID.
     *
     * @param id El identificador único del cliente a consultar.
     * @return Una respuesta HTTP 200 (OK) con la información del cliente.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consultar un cliente", description = "Obtiene los detalles de un cliente por su ID")
    public ResponseEntity<ClienteDTO> obtenerCliente(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerCliente(id));
    }

    /**
     * Obtiene una lista de todos los clientes registrados en el sistema.
     *
     * @return Una respuesta HTTP 200 (OK) con la lista de clientes.
     */
    @GetMapping
    @Operation(summary = "Listar clientes", description = "Obtiene la lista de todos los clientes")
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    /**
     * Actualiza la información de un cliente existente.
     *
     * @param id  El identificador único del cliente a actualizar.
     * @param dto El objeto con los nuevos datos del cliente.
     * @return Una respuesta HTTP 200 (OK) con la información actualizada.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", description = "Actualiza la información de un cliente existente")
    public ResponseEntity<ClienteDTO> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, dto));
    }

    /**
     * Elimina un cliente del sistema dado su ID.
     *
     * @param id El identificador único del cliente a eliminar.
     * @return Una respuesta HTTP 204 (NO CONTENT) indicando la eliminación exitosa.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
