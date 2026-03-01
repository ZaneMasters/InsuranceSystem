package com.example.insurance_system.service;

import com.example.insurance_system.dto.ClienteDTO;
import com.example.insurance_system.exception.ApplicationException;
import com.example.insurance_system.model.Cliente;
import com.example.insurance_system.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar la lógica de negocio para los clientes.
 * Proporciona métodos para registrar, consultar, actualizar y eliminar
 * clientes.
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    /**
     * Crea un nuevo cliente en el sistema.
     * Verifica que el número de documento no esté registrado previamente.
     *
     * @param dto El objeto DTO con la información del cliente.
     * @return El DTO del cliente recién creado.
     * @throws ApplicationException Si el número de documento ya existe.
     */
    @Transactional
    public ClienteDTO crearCliente(ClienteDTO dto) {
        if (clienteRepository.findByNumeroDocumento(dto.getNumeroDocumento()).isPresent()) {
            throw new ApplicationException("Ya existe un cliente con ese número de documento", HttpStatus.BAD_REQUEST);
        }

        Cliente cliente = mapToEntity(dto);
        cliente = clienteRepository.save(cliente);
        return mapToDTO(cliente);
    }

    /**
     * Obtiene los detalles de un cliente específico por su ID.
     *
     * @param id Identificador único del cliente.
     * @return El objeto DTO con la información del cliente.
     * @throws ApplicationException Si no se encuentra un cliente con el ID
     *                              proporcionado.
     */
    @Transactional(readOnly = true)
    public ClienteDTO obtenerCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Cliente no encontrado", HttpStatus.NOT_FOUND));
        return mapToDTO(cliente);
    }

    /**
     * Recupera una lista de todos los clientes registrados.
     *
     * @return Lista de objetos DTO de clientes.
     */
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarClientes() {
        return clienteRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza la información de un cliente existente.
     * Valida que el nuevo número de documento, en caso de haber cambiado, no
     * pertenezca a otro cliente.
     *
     * @param id  Identificador único del cliente a actualizar.
     * @param dto DTO con los datos actualizados del cliente.
     * @return El objeto DTO del cliente actualizado.
     * @throws ApplicationException Si el cliente no existe o el nuevo documento ya
     *                              está en uso.
     */
    @Transactional
    public ClienteDTO actualizarCliente(Long id, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Cliente no encontrado", HttpStatus.NOT_FOUND));

        cliente.setTipoDocumento(dto.getTipoDocumento());
        cliente.setNombres(dto.getNombres());
        cliente.setApellidos(dto.getApellidos());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());

        // Validar si están cambiando el número de documento por uno que ya existe
        if (!cliente.getNumeroDocumento().equals(dto.getNumeroDocumento())) {
            if (clienteRepository.findByNumeroDocumento(dto.getNumeroDocumento()).isPresent()) {
                throw new ApplicationException("El nuevo número de documento ya está en uso por otro cliente",
                        HttpStatus.BAD_REQUEST);
            }
            cliente.setNumeroDocumento(dto.getNumeroDocumento());
        }

        cliente = clienteRepository.save(cliente);
        return mapToDTO(cliente);
    }

    /**
     * Elimina a un cliente del sistema dado su ID.
     *
     * @param id Identificador único del cliente a eliminar.
     * @throws ApplicationException Si el cliente no existe.
     */
    @Transactional
    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Cliente no encontrado", HttpStatus.NOT_FOUND));
        clienteRepository.delete(cliente);
    }

    /**
     * Convierte un objeto ClienteDTO a la entidad Cliente.
     *
     * @param dto El objeto de transferencia de datos.
     * @return La entidad Cliente lista para ser persistida.
     */
    private Cliente mapToEntity(ClienteDTO dto) {
        return Cliente.builder()
                .tipoDocumento(dto.getTipoDocumento())
                .numeroDocumento(dto.getNumeroDocumento())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .fechaNacimiento(dto.getFechaNacimiento())
                .build();
    }

    /**
     * Convierte una entidad Cliente a su respectivo ClienteDTO.
     *
     * @param cliente La entidad de modelo.
     * @return El objeto DTO a exponer mediante la capa de servicios.
     */
    public ClienteDTO mapToDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .id(cliente.getId())
                .tipoDocumento(cliente.getTipoDocumento())
                .numeroDocumento(cliente.getNumeroDocumento())
                .nombres(cliente.getNombres())
                .apellidos(cliente.getApellidos())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .build();
    }
}
