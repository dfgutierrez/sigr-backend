package com.sigr.application.service;

import com.sigr.application.dto.proveedor.ProveedorRequestDTO;
import com.sigr.application.dto.proveedor.ProveedorResponseDTO;
import com.sigr.application.dto.proveedor.ProveedorUpdateDTO;
import com.sigr.application.port.input.ProveedorUseCase;
import com.sigr.application.port.output.ProveedorRepositoryPort;
import com.sigr.domain.entity.Proveedor;
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
public class ProveedorServiceImpl implements ProveedorUseCase {

    private final ProveedorRepositoryPort proveedorRepositoryPort;

    @Override
    public List<ProveedorResponseDTO> findAll() {
        List<Proveedor> proveedores = proveedorRepositoryPort.findAll();
        return proveedores.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProveedorResponseDTO> findAllPaginated(Pageable pageable) {
        Page<Proveedor> proveedoresPage = proveedorRepositoryPort.findAllPaginated(pageable);
        return proveedoresPage.map(this::mapToResponseDTO);
    }

    @Override
    public ProveedorResponseDTO findById(Long id) {
        Proveedor proveedor = proveedorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
        return mapToResponseDTO(proveedor);
    }

    @Override
    public List<ProveedorResponseDTO> findByNombreContaining(String nombre) {
        List<Proveedor> proveedores = proveedorRepositoryPort.findByNombreContaining(nombre);
        return proveedores.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProveedorResponseDTO create(ProveedorRequestDTO request) {
        if (proveedorRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un proveedor con el nombre: " + request.getNombre());
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(request.getNombre());
        proveedor.setTelefono(request.getTelefono());

        Proveedor proveedorGuardado = proveedorRepositoryPort.save(proveedor);
        return mapToResponseDTO(proveedorGuardado);
    }

    @Override
    @Transactional
    public ProveedorResponseDTO update(Long id, ProveedorUpdateDTO request) {
        Proveedor proveedor = proveedorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        if (request.getNombre() != null && 
            !proveedor.getNombre().equals(request.getNombre()) && 
            proveedorRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un proveedor con el nombre: " + request.getNombre());
        }

        if (request.getNombre() != null) {
            proveedor.setNombre(request.getNombre());
        }
        if (request.getTelefono() != null) {
            proveedor.setTelefono(request.getTelefono());
        }

        Proveedor proveedorActualizado = proveedorRepositoryPort.save(proveedor);
        return mapToResponseDTO(proveedorActualizado);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!proveedorRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + id);
        }

        long productosCount = countProductosByProveedorId(id);
        if (productosCount > 0) {
            throw new BusinessException("No se puede eliminar el proveedor porque tiene " + productosCount + " productos asociados");
        }

        proveedorRepositoryPort.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return proveedorRepositoryPort.existsById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return proveedorRepositoryPort.existsByNombre(nombre);
    }

    @Override
    public long countProductosByProveedorId(Long proveedorId) {
        return proveedorRepositoryPort.countProductosByProveedorId(proveedorId);
    }

    private ProveedorResponseDTO mapToResponseDTO(Proveedor proveedor) {
        ProveedorResponseDTO dto = new ProveedorResponseDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setTelefono(proveedor.getTelefono());
        return dto;
    }
}