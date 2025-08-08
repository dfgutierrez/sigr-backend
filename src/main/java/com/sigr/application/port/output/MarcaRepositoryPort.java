package com.sigr.application.port.output;

import com.sigr.domain.entity.Marca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MarcaRepositoryPort {
    
    List<Marca> findAll();
    
    Page<Marca> findAllPaginated(Pageable pageable);
    
    Optional<Marca> findById(Long id);
    
    List<Marca> findByNombreContaining(String nombre);
    
    Marca save(Marca marca);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByNombre(String nombre);
    
    long countVehiculosByMarcaId(Long marcaId);
}