package com.example.insurance_system.service;

import com.example.insurance_system.dto.ClienteDTO;
import com.example.insurance_system.exception.ApplicationException;
import com.example.insurance_system.model.Cliente;
import com.example.insurance_system.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente1;
    private ClienteDTO clienteDTO1;

    @BeforeEach
    void setUp() {
        cliente1 = Cliente.builder()
                .id(1L)
                .tipoDocumento("CC")
                .numeroDocumento("123456789")
                .nombres("Juan")
                .apellidos("Perez")
                .email("juan@test.com")
                .telefono("3001234567")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();

        clienteDTO1 = ClienteDTO.builder()
                .id(1L)
                .tipoDocumento("CC")
                .numeroDocumento("123456789")
                .nombres("Juan")
                .apellidos("Perez")
                .email("juan@test.com")
                .telefono("3001234567")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void crearCliente_Exitoso() {
        when(clienteRepository.findByNumeroDocumento(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente1);

        ClienteDTO result = clienteService.crearCliente(clienteDTO1);

        assertNotNull(result);
        assertEquals("Juan", result.getNombres());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void crearCliente_FallaPorDocumentoExistente() {
        when(clienteRepository.findByNumeroDocumento(anyString())).thenReturn(Optional.of(cliente1));

        assertThrows(ApplicationException.class, () -> clienteService.crearCliente(clienteDTO1));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void obtenerCliente_Exitoso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente1));

        ClienteDTO result = clienteService.obtenerCliente(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void obtenerCliente_FallaPorNoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> clienteService.obtenerCliente(1L));
    }

    @Test
    void listarClientes_Exitoso() {
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente1));

        List<ClienteDTO> result = clienteService.listarClientes();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void actualizarCliente_Exitoso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente1));

        ClienteDTO requestActualizado = ClienteDTO.builder()
                .nombres("Pedro")
                .apellidos("Gomez")
                .email("pedro@test.com")
                .telefono("3007654321")
                .build();

        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClienteDTO result = clienteService.actualizarCliente(1L, requestActualizado);

        assertNotNull(result);
        assertEquals("Pedro", result.getNombres());
        assertEquals("Gomez", result.getApellidos());
    }

    @Test
    void actualizarCliente_FallaPorNoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        ClienteDTO requestActualizado = ClienteDTO.builder().build();

        assertThrows(ApplicationException.class, () -> clienteService.actualizarCliente(1L, requestActualizado));
    }

    @Test
    void eliminarCliente_Exitoso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente1));
        doNothing().when(clienteRepository).delete(cliente1);

        assertDoesNotThrow(() -> clienteService.eliminarCliente(1L));
        verify(clienteRepository).delete(cliente1);
    }

    @Test
    void eliminarCliente_FallaPorNoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> clienteService.eliminarCliente(1L));
        verify(clienteRepository, never()).delete(any(Cliente.class));
    }
}
