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

/**
 * Servicio encargado de gestionar la lógica de negocio para las pólizas de
 * seguros.
 * Evalúa las reglas específicas para pólizas de vida, salud y vehículos.
 */
@Service
@RequiredArgsConstructor
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final ClienteRepository clienteRepository;
    private final BeneficiarioRepository beneficiarioRepository;

    /**
     * Crea una nueva póliza y la asocia a un cliente existente.
     * Evalúa las reglas de negocio dependiendo del tipo de póliza proporcionado.
     *
     * @param dto El objeto DTO con la información de la póliza a crear.
     * @return El DTO de la póliza creada y guardada en el sistema.
     * @throws ApplicationException Si el cliente no existe o se incumplen reglas de
     *                              negocio.
     */
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

    /**
     * Lista todas las pólizas de un cliente determinado.
     *
     * @param clienteId El ID del cliente a consultar.
     * @return Lista de DTOs con las pólizas del cliente.
     * @throws ApplicationException Si el cliente no existe.
     */
    @Transactional(readOnly = true)
    public List<PolizaDTO> listarPolizasPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ApplicationException("Cliente no encontrado", HttpStatus.NOT_FOUND);
        }
        return polizaRepository.findByClienteId(clienteId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Consulta el detalle específico de una póliza, incluyendo sus listas asociadas
     * (vehículos o beneficiarios).
     *
     * @param polizaId El ID de la póliza.
     * @return El DTO de la póliza consultada.
     * @throws ApplicationException Si la póliza no existe.
     */
    @Transactional(readOnly = true)
    public PolizaDTO consultarDetallePoliza(Long polizaId) {
        Poliza poliza = polizaRepository.findById(polizaId)
                .orElseThrow(() -> new ApplicationException("Póliza no encontrada", HttpStatus.NOT_FOUND));
        return mapToDTO(poliza);
    }

    /**
     * Lista los beneficiarios asociados a una póliza en particular.
     * Usado principalmente para pólizas de tipo Vida o Salud.
     *
     * @param polizaId El ID de la póliza.
     * @return Lista de DTOs con los beneficiarios.
     * @throws ApplicationException Si la póliza no existe.
     */
    @Transactional(readOnly = true)
    public List<BeneficiarioDTO> listarBeneficiariosPorPoliza(Long polizaId) {
        Poliza poliza = polizaRepository.findById(polizaId)
                .orElseThrow(() -> new ApplicationException("Póliza no encontrada", HttpStatus.NOT_FOUND));

        return beneficiarioRepository.findByPolizaId(poliza.getId()).stream()
                .map(this::mapBeneficiarioToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Valida las reglas de negocio específicas para pólizas de Vida.
     * - Solo se permite una póliza de este tipo por cliente.
     * - Solo se permiten hasta 2 beneficiarios.
     *
     * @param clienteId El ID del cliente.
     * @param dto       El DTO con los datos de configuración de la póliza.
     * @throws ApplicationException Si se viola alguna de las reglas.
     */
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

    /**
     * Valida las reglas de negocio específicas para pólizas de Salud.
     * - Es requerido especificar un nivel de cobertura.
     *
     * @param dto El DTO con la configuración de la póliza.
     * @throws ApplicationException Si se viola alguna de las reglas.
     */
    private void validarPolizaSalud(PolizaDTO dto) {
        if (dto.getCoberturaSalud() == null) {
            throw new ApplicationException("Las pólizas de SALUD requieren especificar la cobertura_salud.",
                    HttpStatus.BAD_REQUEST);
        }
        // Validación adicional opcional: asegurar que los parentescos coincidan con la
        // cobertura
    }

    /**
     * Calcula dinámicamente la tarifa final de una póliza de salud en base a la
     * cantidad de beneficiarios.
     *
     * @param poliza La instancia de póliza cuyo precio se va a actualizar.
     */
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

    /**
     * Agrega una lista de DTOs de beneficiarios a la entidad Póliza.
     *
     * @param poliza Entidad destino.
     * @param dtos   Lista de beneficiarios a convertir en entidades y agregar.
     */
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

    /**
     * Agrega una lista de DTOs de vehículos a la entidad Póliza.
     *
     * @param poliza Entidad destino.
     * @param dtos   Lista de vehículos a convertir en entidades y agregar.
     */
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

    /**
     * Convierte una entidad Poliza en su equivalente de Transferencia de Datos.
     *
     * @param poliza Entidad de la persistencia.
     * @return El objeto DTO para ser devuelto por la API.
     */
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

    /**
     * Convierte un Beneficiario en su DTO respectivo.
     *
     * @param b Entidad Beneficiario.
     * @return El DTO de Beneficiario.
     */
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

    /**
     * Convierte un VehiculoAsegurado en su DTO respectivo.
     *
     * @param v Entidad VehiculoAsegurado.
     * @return El DTO de VehiculoAsegurado.
     */
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
