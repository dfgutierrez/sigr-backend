package com.sigr.application.service;

import com.sigr.application.dto.marca.MarcaRequestDTO;
import com.sigr.application.dto.marca.MarcaResponseDTO;
import com.sigr.application.dto.marca.MarcaUpdateDTO;
import com.sigr.application.port.input.MarcaUseCase;
import com.sigr.application.port.output.MarcaRepositoryPort;
import com.sigr.domain.entity.Marca;
import com.sigr.domain.exception.ResourceNotFoundException;
import com.sigr.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarcaServiceImpl implements MarcaUseCase {

    private final MarcaRepositoryPort marcaRepositoryPort;

    @Override
    public List<MarcaResponseDTO> findAll() {
        List<Marca> marcas = marcaRepositoryPort.findAll();
        return marcas.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MarcaResponseDTO> findAllPaginated(Pageable pageable) {
        Page<Marca> marcasPage = marcaRepositoryPort.findAllPaginated(pageable);
        return marcasPage.map(this::mapToResponseDTO);
    }

    @Override
    public MarcaResponseDTO findById(Long id) {
        Marca marca = marcaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + id));
        return mapToResponseDTO(marca);
    }

    @Override
    public List<MarcaResponseDTO> findByNombreContaining(String nombre) {
        List<Marca> marcas = marcaRepositoryPort.findByNombreContaining(nombre);
        return marcas.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MarcaResponseDTO create(MarcaRequestDTO request) {
        // Validar que no exista una marca con el mismo nombre
        if (marcaRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una marca con el nombre: " + request.getNombre());
        }

        Marca marca = new Marca();
        marca.setNombre(request.getNombre());

        Marca marcaGuardada = marcaRepositoryPort.save(marca);
        return mapToResponseDTO(marcaGuardada);
    }

    @Override
    @Transactional
    public MarcaResponseDTO update(Long id, MarcaUpdateDTO request) {
        Marca marca = marcaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + id));

        // Validar que no exista otra marca con el mismo nombre
        if (request.getNombre() != null && 
            !marca.getNombre().equals(request.getNombre()) && 
            marcaRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una marca con el nombre: " + request.getNombre());
        }

        // Actualizar solo los campos que no son null
        if (request.getNombre() != null) {
            marca.setNombre(request.getNombre());
        }

        Marca marcaActualizada = marcaRepositoryPort.save(marca);
        return mapToResponseDTO(marcaActualizada);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!marcaRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Marca no encontrada con ID: " + id);
        }

        // Verificar si la marca tiene vehículos asociados
        long vehiculosCount = countVehiculosByMarcaId(id);
        if (vehiculosCount > 0) {
            throw new BusinessException("No se puede eliminar la marca porque tiene " + vehiculosCount + " vehículos asociados");
        }

        marcaRepositoryPort.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return marcaRepositoryPort.existsById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return marcaRepositoryPort.existsByNombre(nombre);
    }

    @Override
    public long countVehiculosByMarcaId(Long marcaId) {
        return marcaRepositoryPort.countVehiculosByMarcaId(marcaId);
    }

    private MarcaResponseDTO mapToResponseDTO(Marca marca) {
        MarcaResponseDTO dto = new MarcaResponseDTO();
        dto.setId(marca.getId());
        dto.setNombre(marca.getNombre());
        return dto;
    }
}