package com.sigr.infrastructure.adapter.output;

import com.sigr.application.port.output.ProveedorRepositoryPort;
import com.sigr.domain.entity.Proveedor;
import com.sigr.domain.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProveedorRepositoryAdapter implements ProveedorRepositoryPort {

    private final ProveedorRepository proveedorRepository;

    @Override
    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    @Override
    public Page<Proveedor> findAllPaginated(Pageable pageable) {
        return proveedorRepository.findAll(pageable);
    }

    @Override
    public Optional<Proveedor> findById(Long id) {
        return proveedorRepository.findById(id);
    }

    @Override
    public List<Proveedor> findByNombreContaining(String nombre) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Override
    public void deleteById(Long id) {
        proveedorRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return proveedorRepository.existsById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return proveedorRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public long countProductosByProveedorId(Long proveedorId) {
        return proveedorRepository.countProductosByProveedorId(proveedorId);
    }
}