package com.sigr.application.service;

import com.sigr.application.dto.sede.SedeRequestDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import com.sigr.application.mapper.SedeMapper;
import com.sigr.application.port.input.SedeUseCase;
import com.sigr.application.port.output.SedeRepositoryPort;
import com.sigr.domain.entity.Sede;
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
public class SedeServiceImpl implements SedeUseCase {

    private final SedeRepositoryPort sedeRepositoryPort;
    private final SedeMapper sedeMapper;

    @Override
    public List<SedeResponseDTO> findAll() {
        log.debug("Finding all sedes");
        List<Sede> sedes = sedeRepositoryPort.findAll();
        return sedes.stream()
                .map(this::buildSedeResponseWithStats)
                .toList();
    }

    @Override
    public Page<SedeResponseDTO> findAllPaginated(Pageable pageable) {
        log.debug("Finding all sedes paginated with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Sede> sedesPage = sedeRepositoryPort.findAllPaginated(pageable);
        return sedesPage.map(this::buildSedeResponseWithStats);
    }

    @Override
    public SedeResponseDTO findById(Long id) {
        log.debug("Finding sede by id: {}", id);
        Sede sede = sedeRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con ID: " + id));
        return buildSedeResponseWithStats(sede);
    }

    @Override
    @Transactional
    public SedeResponseDTO create(SedeRequestDTO request) {
        log.debug("Creating new sede with name: {}", request.getNombre());
        
        if (sedeRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una sede con el nombre: " + request.getNombre());
        }

        Sede sede = sedeMapper.toEntity(request);
        Sede savedSede = sedeRepositoryPort.save(sede);
        
        log.info("Sede created successfully with ID: {}", savedSede.getId());
        return buildSedeResponseWithStats(savedSede);
    }

    @Override
    @Transactional
    public SedeResponseDTO update(Long id, SedeRequestDTO request) {
        log.debug("Updating sede with id: {}", id);
        
        Sede existingSede = sedeRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con ID: " + id));

        if (!existingSede.getNombre().equals(request.getNombre()) && 
            sedeRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una sede con el nombre: " + request.getNombre());
        }

        sedeMapper.updateEntityFromDTO(request, existingSede);
        Sede updatedSede = sedeRepositoryPort.save(existingSede);
        
        log.info("Sede updated successfully with ID: {}", updatedSede.getId());
        return buildSedeResponseWithStats(updatedSede);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting sede with id: {}", id);
        
        if (!sedeRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Sede no encontrada con ID: " + id);
        }

        long vehiculosCount = sedeRepositoryPort.countVehiculosBySedeId(id);
        long productosCount = sedeRepositoryPort.countInventarioBySedeId(id);
        
        if (vehiculosCount > 0 || productosCount > 0) {
            throw new BusinessException("No se puede eliminar la sede porque tiene " + 
                    vehiculosCount + " vehículo(s) y " + productosCount + " producto(s) asociado(s)");
        }

        sedeRepositoryPort.deleteById(id);
        log.info("Sede deleted successfully with ID: {}", id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        log.debug("Checking if sede exists by name: {}", nombre);
        return sedeRepositoryPort.existsByNombre(nombre);
    }

    @Override
    public List<SedeResponseDTO> findByNombreContaining(String nombre) {
        log.debug("Finding sedes by name containing: {}", nombre);
        List<Sede> sedes = sedeRepositoryPort.findByNombreContaining(nombre);
        return sedes.stream()
                .map(this::buildSedeResponseWithStats)
                .toList();
    }

    private SedeResponseDTO buildSedeResponseWithStats(Sede sede) {
        Integer cantidadVehiculos = Math.toIntExact(sedeRepositoryPort.countVehiculosBySedeId(sede.getId()));
        Integer cantidadProductos = Math.toIntExact(sedeRepositoryPort.countInventarioBySedeId(sede.getId()));
        
        return sedeMapper.toResponseDTO(sede, cantidadVehiculos, cantidadProductos);
    }
}