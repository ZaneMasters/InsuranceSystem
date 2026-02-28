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
import com.example.insurance_system.model.enums.TipoPoliza;
import com.example.insurance_system.repository.BeneficiarioRepository;
import com.example.insurance_system.repository.ClienteRepository;
import com.example.insurance_system.repository.PolizaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final ClienteRepository clienteRepository;
    private final BeneficiarioRepository beneficiarioRepository;

    @Transactional
    public PolizaDTO crearPoliza(PolizaDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ApplicationException("Cliente no encontrado", HttpStatus.NOT_FOUND));

        Poliza poliza = Poliza.builder()
                .numeroPoliza(dto.getNumeroPoliza())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .tipoPoliza(dto.getTipoPoliza())
                .coberturaSalud(dto.getCoberturaSalud())
                .valorAsegurado(dto.getValorAsegurado())
                .tarifaBase(dto.getTarifaBase())
                .cliente(cliente)
                .build();

        // Reglas de negocio según tipo de póliza
        switch (dto.getTipoPoliza()) {
            case VIDA:
                validarPolizaVida(cliente.getId(), dto);
                agregarBeneficiarios(poliza, dto.getBeneficiarios());
                break;
            case VEHICULO:
                agregarVehiculos(poliza, dto.getVehiculos());
                break;
            case SALUD:
                validarPolizaSalud(dto);
                agregarBeneficiarios(poliza, dto.getBeneficiarios());
                calcularTarifaSalud(poliza);
                break;
        }

        poliza = polizaRepository.save(poliza);
        return mapToDTO(poliza);
    }

    @Transactional(readOnly = true)
    public List<PolizaDTO> listarPolizasPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ApplicationException("Cliente no encontrado", HttpStatus.NOT_FOUND);
        }
        return polizaRepository.findByClienteId(clienteId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PolizaDTO consultarDetallePoliza(Long polizaId) {
        Poliza poliza = polizaRepository.findById(polizaId)
                .orElseThrow(() -> new ApplicationException("Póliza no encontrada", HttpStatus.NOT_FOUND));
        return mapToDTO(poliza);
    }

    @Transactional(readOnly = true)
    public List<BeneficiarioDTO> listarBeneficiariosPorPoliza(Long polizaId) {
        Poliza poliza = polizaRepository.findById(polizaId)
                .orElseThrow(() -> new ApplicationException("Póliza no encontrada", HttpStatus.NOT_FOUND));

        return beneficiarioRepository.findByPolizaId(poliza.getId()).stream()
                .map(this::mapBeneficiarioToDTO)
                .collect(Collectors.toList());
    }

    private void validarPolizaVida(Long clienteId, PolizaDTO dto) {
        if (polizaRepository.existsByClienteIdAndTipoPoliza(clienteId, TipoPoliza.VIDA)) {
            throw new ApplicationException("El cliente ya posee una póliza de VIDA. Solo puede existir 1 por cliente.",
                    HttpStatus.BAD_REQUEST);
        }
        if (dto.getBeneficiarios() != null && dto.getBeneficiarios().size() > 2) {
            throw new ApplicationException("Una póliza de VIDA solo puede tener hasta 2 beneficiarios.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validarPolizaSalud(PolizaDTO dto) {
        if (dto.getCoberturaSalud() == null) {
            throw new ApplicationException("Las pólizas de SALUD requieren especificar la cobertura_salud.",
                    HttpStatus.BAD_REQUEST);
        }
        // Validación adicional opcional: asegurar que los parentescos coincidan con la
        // cobertura
    }

    private void calcularTarifaSalud(Poliza poliza) {
        if (poliza.getBeneficiarios() == null || poliza.getBeneficiarios().isEmpty())
            return;

        // Regla: cobro extra por beneficiario dependiendo del tipo de cobertura (Ej:
        // +10% de la base por cada beneficiario)
        BigDecimal tarifaBase = poliza.getTarifaBase();
        BigDecimal recargoPorPersona = tarifaBase.multiply(new BigDecimal("0.10"));
        long numFamiliares = poliza.getBeneficiarios().size();

        BigDecimal tarifaFinal = tarifaBase.add(recargoPorPersona.multiply(new BigDecimal(numFamiliares)));
        poliza.setTarifaBase(tarifaFinal);
    }

    private void agregarBeneficiarios(Poliza poliza, List<BeneficiarioDTO> dtos) {
        if (dtos != null) {
            for (BeneficiarioDTO bDto : dtos) {
                Beneficiario b = Beneficiario.builder()
                        .nombres(bDto.getNombres())
                        .apellidos(bDto.getApellidos())
                        .tipoDocumento(bDto.getTipoDocumento())
                        .numeroDocumento(bDto.getNumeroDocumento())
                        .parentesco(bDto.getParentesco())
                        .build();
                poliza.addBeneficiario(b);
            }
        }
    }

    private void agregarVehiculos(Poliza poliza, List<VehiculoAseguradoDTO> dtos) {
        if (dtos != null) {
            for (VehiculoAseguradoDTO vDto : dtos) {
                VehiculoAsegurado v = VehiculoAsegurado.builder()
                        .placa(vDto.getPlaca())
                        .marca(vDto.getMarca())
                        .modelo(vDto.getModelo())
                        .anio(vDto.getAnio())
                        .build();
                poliza.addVehiculo(v);
            }
        }
    }

    private PolizaDTO mapToDTO(Poliza poliza) {
        return PolizaDTO.builder()
                .id(poliza.getId())
                .clienteId(poliza.getCliente().getId())
                .numeroPoliza(poliza.getNumeroPoliza())
                .fechaInicio(poliza.getFechaInicio())
                .fechaFin(poliza.getFechaFin())
                .tipoPoliza(poliza.getTipoPoliza())
                .coberturaSalud(poliza.getCoberturaSalud())
                .valorAsegurado(poliza.getValorAsegurado())
                .tarifaBase(poliza.getTarifaBase())
                .beneficiarios(
                        poliza.getBeneficiarios().stream().map(this::mapBeneficiarioToDTO).collect(Collectors.toList()))
                .vehiculos(poliza.getVehiculos().stream().map(this::mapVehiculoToDTO).collect(Collectors.toList()))
                .build();
    }

    private BeneficiarioDTO mapBeneficiarioToDTO(Beneficiario b) {
        return BeneficiarioDTO.builder()
                .id(b.getId())
                .nombres(b.getNombres())
                .apellidos(b.getApellidos())
                .tipoDocumento(b.getTipoDocumento())
                .numeroDocumento(b.getNumeroDocumento())
                .parentesco(b.getParentesco())
                .build();
    }

    private VehiculoAseguradoDTO mapVehiculoToDTO(VehiculoAsegurado v) {
        return VehiculoAseguradoDTO.builder()
                .id(v.getId())
                .placa(v.getPlaca())
                .marca(v.getMarca())
                .modelo(v.getModelo())
                .anio(v.getAnio())
                .build();
    }
}
