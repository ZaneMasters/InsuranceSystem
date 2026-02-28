package com.example.insurance_system.controller;

import com.example.insurance_system.dto.ClienteDTO;
import com.example.insurance_system.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        clienteDTO = ClienteDTO.builder()
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
    void crearCliente() throws Exception {
        when(clienteService.crearCliente(any(ClienteDTO.class))).thenReturn(clienteDTO);

        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombres").value("Juan"));
    }

    @Test
    void obtenerCliente() throws Exception {
        when(clienteService.obtenerCliente(1L)).thenReturn(clienteDTO);

        mockMvc.perform(get("/api/v1/clientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void listarClientes() throws Exception {
        when(clienteService.listarClientes()).thenReturn(Arrays.asList(clienteDTO));

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void eliminarCliente() throws Exception {
        doNothing().when(clienteService).eliminarCliente(1L);

        mockMvc.perform(delete("/api/v1/clientes/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
