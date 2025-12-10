package com.sigr.application.port.output;

import com.sigr.domain.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepositoryPort {
    
    List<Proveedor> findAll();
    
    Page<Proveedor> findAllPaginated(Pageable pageable);
    
    Optional<Proveedor> findById(Long id);
    
    List<Proveedor> findByNombreContaining(String nombre);
    
    Proveedor save(Proveedor proveedor);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByNombre(String nombre);
    
    long countProductosByProveedorId(Long proveedorId);
}