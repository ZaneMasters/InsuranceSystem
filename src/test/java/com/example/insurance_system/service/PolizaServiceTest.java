package com.example.insurance_system.service;

import com.example.insurance_system.dto.BeneficiarioDTO;
import com.example.insurance_system.dto.PolizaDTO;
import com.example.insurance_system.exception.ApplicationException;
import com.example.insurance_system.model.Cliente;
import com.example.insurance_system.model.Poliza;
import com.example.insurance_system.model.enums.CoberturaSalud;
import com.example.insurance_system.model.enums.TipoPoliza;
import com.example.insurance_system.repository.BeneficiarioRepository;
import com.example.insurance_system.repository.ClienteRepository;
import com.example.insurance_system.repository.PolizaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolizaServiceTest {

    @Mock
    private PolizaRepository polizaRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @InjectMocks
    private PolizaService polizaService;

    private Cliente cliente;
    private PolizaDTO polizaDTOVida;
    private PolizaDTO polizaDTOSalud;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(1L).build();

        polizaDTOVida = PolizaDTO.builder()
                .numeroPoliza("V-100")
                .clienteId(1L)
                .tipoPoliza(TipoPoliza.VIDA)
                .valorAsegurado(new BigDecimal("100000"))
                .tarifaBase(new BigDecimal("100"))
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusYears(1))
                .build();

        polizaDTOSalud = PolizaDTO.builder()
                .numeroPoliza("S-100")
                .clienteId(1L)
                .tipoPoliza(TipoPoliza.SALUD)
                .coberturaSalud(CoberturaSalud.CLIENTE_ESPOSA_E_HIJOS)
                .valorAsegurado(new BigDecimal("50000"))
                .tarifaBase(new BigDecimal("200"))
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusYears(1))
                .beneficiarios(Arrays.asList(
                        BeneficiarioDTO.builder().nombres("A").apellidos("B").build(),
                        BeneficiarioDTO.builder().nombres("C").apellidos("D").build()))
                .build();
    }

    @Test
    void crearPolizaVida_Exitosa() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(polizaRepository.existsByClienteIdAndTipoPoliza(1L, TipoPoliza.VIDA)).thenReturn(false);
        when(polizaRepository.save(any(Poliza.class))).thenAnswer(i -> {
            Poliza p = i.getArgument(0);
            p.setId(10L);
            return p;
        });

        PolizaDTO result = polizaService.crearPoliza(polizaDTOVida);

        assertNotNull(result);
        assertEquals(TipoPoliza.VIDA, result.getTipoPoliza());
    }

    @Test
    void crearPolizaVida_FallaPorPolizaExistente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(polizaRepository.existsByClienteIdAndTipoPoliza(1L, TipoPoliza.VIDA)).thenReturn(true);

        assertThrows(ApplicationException.class, () -> polizaService.crearPoliza(polizaDTOVida));
    }

    @Test
    void crearPolizaVida_FallaPorExcesoBeneficiarios() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(polizaRepository.existsByClienteIdAndTipoPoliza(1L, TipoPoliza.VIDA)).thenReturn(false);

        polizaDTOVida.setBeneficiarios(Arrays.asList(
                BeneficiarioDTO.builder().build(),
                BeneficiarioDTO.builder().build(),
                BeneficiarioDTO.builder().build() // 3 beneficiarios
        ));

        assertThrows(ApplicationException.class, () -> polizaService.crearPoliza(polizaDTOVida));
    }

    @Test
    void crearPolizaSalud_CalculaTarifaCorrectamente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(polizaRepository.save(any(Poliza.class))).thenAnswer(i -> {
            Poliza p = i.getArgument(0);
            p.setId(10L);
            return p;
        });

        PolizaDTO result = polizaService.crearPoliza(polizaDTOSalud);

        assertNotNull(result);
        // Tarifa base = 200. Beneficiarios = 2.
        // Recargo = 10% de 200 = 20 por persona. 20 * 2 = 40. Total = 240.
        // Asumiendo que mi lógica usa BigDecimal("0.10") de la tarifa base.
        assertEquals(0, new BigDecimal("240.00").compareTo(result.getTarifaBase()));
    }
}
