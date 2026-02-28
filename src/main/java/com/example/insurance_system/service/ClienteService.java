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

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteDTO crearCliente(ClienteDTO dto) {
        if (clienteRepository.findByNumeroDocumento(dto.getNumeroDocumento()).isPresent()) {
            throw new ApplicationException("Ya existe un cliente con ese número de documento", HttpStatus.BAD_REQUEST);
        }

        Cliente cliente = mapToEntity(dto);
        cliente = clienteRepository.save(cliente);
        return mapToDTO(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteDTO obtenerCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Cliente no encontrado", HttpStatus.NOT_FOUND));
        return mapToDTO(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarClientes() {
        return clienteRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

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

    @Transactional
    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Cliente no encontrado", HttpStatus.NOT_FOUND));
        clienteRepository.delete(cliente);
    }

    // Mappers
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
