package com.example.insurance_system.controller;

import com.example.insurance_system.dto.PolizaDTO;
import com.example.insurance_system.model.enums.TipoPoliza;
import com.example.insurance_system.service.PolizaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolizaController.class)
class PolizaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolizaService polizaService;

    @Autowired
    private ObjectMapper objectMapper;

    private PolizaDTO polizaDTO;

    @BeforeEach
    void setUp() {
        polizaDTO = PolizaDTO.builder()
                .id(1L)
                .clienteId(1L)
                .numeroPoliza("POL-123")
                .tipoPoliza(TipoPoliza.VIDA)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusYears(1))
                .valorAsegurado(new BigDecimal("1000"))
                .build();
    }

    @Test
    void crearPoliza() throws Exception {
        when(polizaService.crearPoliza(any(PolizaDTO.class))).thenReturn(polizaDTO);

        mockMvc.perform(post("/api/v1/polizas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(polizaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroPoliza").value("POL-123"));
    }

    @Test
    void listarPolizasPorCliente() throws Exception {
        when(polizaService.listarPolizasPorCliente(1L)).thenReturn(Arrays.asList(polizaDTO));

        mockMvc.perform(get("/api/v1/polizas/cliente/{clienteId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroPoliza").value("POL-123"));
    }
}
