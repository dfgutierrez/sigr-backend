package com.sigr.application.service;

import com.sigr.application.dto.vehiculo.VehiculoRequestDTO;
import com.sigr.application.dto.vehiculo.VehiculoResponseDTO;
import com.sigr.application.dto.vehiculo.VehiculoUpdateDTO;
import com.sigr.application.dto.vehiculo.VehiculoSearchResultDTO;
import com.sigr.application.mapper.VehiculoMapper;
import com.sigr.application.port.input.VehiculoUseCase;
import com.sigr.application.port.output.MarcaRepositoryPort;
import com.sigr.application.port.output.SedeRepositoryPort;
import com.sigr.application.port.output.VehiculoRepositoryPort;
import com.sigr.domain.entity.Marca;
import com.sigr.domain.entity.Sede;
import com.sigr.domain.entity.Vehiculo;
import com.sigr.domain.exception.BusinessException;
import com.sigr.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehiculoServiceImpl implements VehiculoUseCase {

    private final VehiculoRepositoryPort vehiculoRepositoryPort;
    private final MarcaRepositoryPort marcaRepositoryPort;
    private final SedeRepositoryPort sedeRepositoryPort;
    private final VehiculoMapper vehiculoMapper;

    @Override
    public List<VehiculoResponseDTO> findAll() {
        log.debug("Finding all vehicles");
        List<Vehiculo> vehiculos = vehiculoRepositoryPort.findAll();
        return vehiculoMapper.toResponseDTOList(vehiculos);
    }

    @Override
    public Page<VehiculoResponseDTO> findAllPaginated(Pageable pageable) {
        log.debug("Finding paginated vehicles with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Vehiculo> vehiculosPage = vehiculoRepositoryPort.findAllPaginated(pageable);
        return vehiculosPage.map(vehiculoMapper::toResponseDTO);
    }

    @Override
    public VehiculoResponseDTO findById(Long id) {
        log.debug("Finding vehicle by id: {}", id);
        Vehiculo vehiculo = vehiculoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "id", id));
        return vehiculoMapper.toResponseDTO(vehiculo);
    }

    @Override
    @Transactional
    public VehiculoResponseDTO create(VehiculoRequestDTO request) {
        log.debug("Creating new vehicle with placa: {}", request.getPlaca());
        
        validateVehiculoRequest(request);
        
        if (vehiculoRepositoryPort.existsByPlaca(request.getPlaca())) {
            throw new BusinessException("Ya existe un vehículo con la placa: " + request.getPlaca());
        }

        Vehiculo vehiculo = vehiculoMapper.toEntity(request);
        
        setVehiculoRelations(vehiculo, request);
        
        Vehiculo savedVehiculo = vehiculoRepositoryPort.save(vehiculo);
        log.info("Vehicle created successfully with id: {}", savedVehiculo.getId());
        
        return vehiculoMapper.toResponseDTO(savedVehiculo);
    }

    @Override
    @Transactional
    public VehiculoResponseDTO update(Long id, VehiculoUpdateDTO request) {
        log.debug("Updating vehicle with id: {}", id);
        
        validateVehiculoUpdateRequest(request);
        
        Vehiculo existingVehiculo = vehiculoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "id", id));

        if (request.getPlaca() != null && !existingVehiculo.getPlaca().equals(request.getPlaca()) 
            && vehiculoRepositoryPort.existsByPlaca(request.getPlaca())) {
            throw new BusinessException("Ya existe un vehículo con la placa: " + request.getPlaca());
        }

        vehiculoMapper.updateEntityFromUpdateDTO(request, existingVehiculo);
        setVehiculoUpdateRelations(existingVehiculo, request);
        
        Vehiculo updatedVehiculo = vehiculoRepositoryPort.save(existingVehiculo);
        log.info("Vehicle updated successfully with id: {}", updatedVehiculo.getId());
        
        return vehiculoMapper.toResponseDTO(updatedVehiculo);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting vehicle with id: {}", id);
        
        if (!vehiculoRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Vehículo", "id", id);
        }
        
        vehiculoRepositoryPort.deleteById(id);
        log.info("Vehicle deleted successfully with id: {}", id);
    }

    @Override
    public List<VehiculoResponseDTO> findByTipo(String tipo) {
        log.debug("Finding vehicles by tipo: {}", tipo);
        List<Vehiculo> vehiculos = vehiculoRepositoryPort.findByTipo(tipo);
        return vehiculoMapper.toResponseDTOList(vehiculos);
    }

    @Override
    public List<VehiculoResponseDTO> findByPlacaContaining(String placa) {
        log.debug("Finding vehicles by placa containing: {}", placa);
        List<Vehiculo> vehiculos = vehiculoRepositoryPort.findByPlacaContaining(placa);
        return vehiculoMapper.toResponseDTOList(vehiculos);
    }

    @Override
    public List<VehiculoResponseDTO> findByEstado(Boolean estado) {
        log.debug("Finding vehicles by estado: {}", estado);
        List<Vehiculo> vehiculos = vehiculoRepositoryPort.findByEstado(estado);
        return vehiculoMapper.toResponseDTOList(vehiculos);
    }

    @Override
    public List<VehiculoResponseDTO> findBySede(Long sedeId) {
        log.debug("Finding vehicles by sede: {}", sedeId);
        List<Vehiculo> vehiculos = vehiculoRepositoryPort.findBySedeId(sedeId);
        return vehiculoMapper.toResponseDTOList(vehiculos);
    }

    @Override
    public VehiculoSearchResultDTO searchByPlaca(String placa) {
        log.debug("Searching vehicle by placa: {}", placa);
        
        List<Vehiculo> vehiculos = vehiculoRepositoryPort.findByPlacaContaining(placa.toUpperCase());
        
        if (!vehiculos.isEmpty()) {
            // Si encontramos vehículos, retornamos el primero (búsqueda exacta o más cercana)
            Vehiculo vehiculoExacto = vehiculos.stream()
                    .filter(v -> v.getPlaca().equalsIgnoreCase(placa))
                    .findFirst()
                    .orElse(vehiculos.get(0));
            
            VehiculoResponseDTO vehiculoDTO = vehiculoMapper.toResponseDTO(vehiculoExacto);
            return VehiculoSearchResultDTO.encontrado(vehiculoDTO);
        }
        
        return VehiculoSearchResultDTO.noEncontrado(placa.toUpperCase());
    }


    @Override
    public VehiculoResponseDTO findByPlaca(String placa) {
        log.debug("Finding vehicle by placa: {}", placa);
        
        List<Vehiculo> vehiculos = vehiculoRepositoryPort.findByPlacaContaining(placa.toUpperCase());
        
        // Buscar coincidencia exacta primero
        Vehiculo vehiculoExacto = vehiculos.stream()
                .filter(v -> v.getPlaca().equalsIgnoreCase(placa))
                .findFirst()
                .orElse(null);
        
        if (vehiculoExacto != null) {
            return vehiculoMapper.toResponseDTO(vehiculoExacto);
        }
        
        throw new ResourceNotFoundException("Vehículo", "placa", placa.toUpperCase());
    }

    private void validateVehiculoRequest(VehiculoRequestDTO request) {
        if (request.getMarcaId() != null && !marcaRepositoryPort.existsById(request.getMarcaId())) {
            throw new ResourceNotFoundException("Marca", "id", request.getMarcaId());
        }
        
        if (request.getSedeId() != null && !sedeRepositoryPort.existsById(request.getSedeId())) {
            throw new ResourceNotFoundException("Sede", "id", request.getSedeId());
        }
    }

    private void setVehiculoRelations(Vehiculo vehiculo, VehiculoRequestDTO request) {
        Marca marca = null;
        Sede sede = null;
        
        if (request.getMarcaId() != null) {
            marca = marcaRepositoryPort.findById(request.getMarcaId()).orElse(null);
        }
        
        if (request.getSedeId() != null) {
            sede = sedeRepositoryPort.findById(request.getSedeId()).orElse(null);
        }
        
        vehiculoMapper.setRelations(vehiculo, marca, sede);
    }

    private void validateVehiculoUpdateRequest(VehiculoUpdateDTO request) {
        if (request.getMarcaId() != null && !marcaRepositoryPort.existsById(request.getMarcaId())) {
            throw new ResourceNotFoundException("Marca", "id", request.getMarcaId());
        }
        
        if (request.getSedeId() != null && !sedeRepositoryPort.existsById(request.getSedeId())) {
            throw new ResourceNotFoundException("Sede", "id", request.getSedeId());
        }
    }

    private void setVehiculoUpdateRelations(Vehiculo vehiculo, VehiculoUpdateDTO request) {
        if (request.getMarcaId() != null) {
            Marca marca = marcaRepositoryPort.findById(request.getMarcaId()).orElse(null);
            vehiculo.setMarca(marca);
        }
        
        if (request.getSedeId() != null) {
            Sede sede = sedeRepositoryPort.findById(request.getSedeId()).orElse(null);
            vehiculo.setSede(sede);
        }
    }
}