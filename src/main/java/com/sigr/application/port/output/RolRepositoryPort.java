package com.sigr.application.port.output;

import com.sigr.domain.entity.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RolRepositoryPort {
    
    List<Rol> findAll();
    
    Page<Rol> findAllPaginated(Pageable pageable);
    
    Optional<Rol> findById(Long id);
    
    Rol save(Rol rol);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByNombre(String nombre);
    
    List<Rol> findByIdIn(List<Long> ids);
    
    List<Rol> findByNombreContaining(String nombre);
    
    long countUsuariosByRolId(Long rolId);
    
    long countMenusByRolId(Long rolId);
}