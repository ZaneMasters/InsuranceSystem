package com.example.insurance_system.service;

import com.example.insurance_system.dto.BeneficiarioDTO;
import com.example.insurance_system.dto.PolizaDTO;
import com.example.insurance_system.dto.VehiculoAseguradoDTO;
import com.example.insurance_system.exception.ApplicationException;
import com.example.insurance_system.model.Beneficiario;
import com.example.insurance_system.model.Cliente;
import com.example.insurance_system.model.Poliza;
import com.example.insurance_system.model.VehiculoAsegurado;
import com.example.insurance_system.model.enums.CoberturaSalud;
import com.example.insurance_system.model.enums.Parentesco;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private PolizaDTO polizaDTOVehiculo;

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
                        BeneficiarioDTO.builder().nombres("A").apellidos("B").tipoDocumento("CC").numeroDocumento("123")
                                .parentesco(Parentesco.HIJO).build(),
                        BeneficiarioDTO.builder().nombres("C").apellidos("D").tipoDocumento("CE").numeroDocumento("456")
                                .parentesco(Parentesco.CONYUGE).build()))
                .build();

        polizaDTOVehiculo = PolizaDTO.builder()
                .numeroPoliza("VE-100")
                .clienteId(1L)
                .tipoPoliza(TipoPoliza.VEHICULO)
                .valorAsegurado(new BigDecimal("30000"))
                .tarifaBase(new BigDecimal("150"))
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusYears(1))
                .vehiculos(Collections.singletonList(
                        VehiculoAseguradoDTO.builder().placa("ABC-123").marca("Toyota").modelo("Corolla").anio(2022)
                                .build()))
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
    void crearPolizaVehiculo_Exitosa() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(polizaRepository.save(any(Poliza.class))).thenAnswer(i -> {
            Poliza p = i.getArgument(0);
            p.setId(20L);
            return p;
        });

        PolizaDTO result = polizaService.crearPoliza(polizaDTOVehiculo);

        assertNotNull(result);
        assertEquals(TipoPoliza.VEHICULO, result.getTipoPoliza());
        assertEquals(1, result.getVehiculos().size());
        assertEquals("ABC-123", result.getVehiculos().get(0).getPlaca());
    }

    @Test
    void crearPoliza_FallaPorClienteNoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> polizaService.crearPoliza(polizaDTOVida));
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
        assertEquals(0, new BigDecimal("240.00").compareTo(result.getTarifaBase()));
    }

    @Test
    void crearPolizaSalud_FallaPorFaltaCobertura() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        polizaDTOSalud.setCoberturaSalud(null);

        assertThrows(ApplicationException.class, () -> polizaService.crearPoliza(polizaDTOSalud));
    }

    @Test
    void listarPolizasPorCliente_Exitoso() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        Poliza poliza1 = Poliza.builder().id(10L).tipoPoliza(TipoPoliza.VIDA).cliente(cliente).build();
        when(polizaRepository.findByClienteId(1L)).thenReturn(Collections.singletonList(poliza1));

        List<PolizaDTO> result = polizaService.listarPolizasPorCliente(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void listarPolizasPorCliente_FallaPorClienteNoEncontrado() {
        when(clienteRepository.existsById(1L)).thenReturn(false);

        assertThrows(ApplicationException.class, () -> polizaService.listarPolizasPorCliente(1L));
    }

    @Test
    void consultarDetallePoliza_Exitoso() {
        Poliza poliza = Poliza.builder().id(10L).tipoPoliza(TipoPoliza.VIDA).cliente(cliente).build();
        when(polizaRepository.findById(10L)).thenReturn(Optional.of(poliza));

        PolizaDTO result = polizaService.consultarDetallePoliza(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void consultarDetallePoliza_FallaPorNoEncontrada() {
        when(polizaRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> polizaService.consultarDetallePoliza(10L));
    }

    @Test
    void listarBeneficiariosPorPoliza_Exitoso() {
        Poliza poliza = Poliza.builder().id(10L).tipoPoliza(TipoPoliza.VIDA).cliente(cliente).build();
        when(polizaRepository.findById(10L)).thenReturn(Optional.of(poliza));

        Beneficiario b1 = Beneficiario.builder().id(100L).nombres("A").apellidos("B").tipoDocumento("CC")
                .numeroDocumento("123").parentesco(Parentesco.HIJO).build();
        when(beneficiarioRepository.findByPolizaId(10L)).thenReturn(Collections.singletonList(b1));

        List<BeneficiarioDTO> result = polizaService.listarBeneficiariosPorPoliza(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getNombres());
    }

    @Test
    void listarBeneficiariosPorPoliza_FallaPorPolizaNoEncontrada() {
        when(polizaRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> polizaService.listarBeneficiariosPorPoliza(10L));
    }
}
