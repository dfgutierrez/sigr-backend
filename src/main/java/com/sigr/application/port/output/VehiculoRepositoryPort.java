package com.sigr.application.port.output;

import com.sigr.domain.entity.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepositoryPort {
    
    List<Vehiculo> findAll();
    
    Page<Vehiculo> findAllPaginated(Pageable pageable);
    
    Optional<Vehiculo> findById(Long id);
    
    Vehiculo save(Vehiculo vehiculo);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByPlaca(String placa);
    
    List<Vehiculo> findByTipo(String tipo);
    
    List<Vehiculo> findByPlacaContaining(String placa);
    
    List<Vehiculo> findByEstado(Boolean estado);
    
    List<Vehiculo> findBySedeId(Long sedeId);
}