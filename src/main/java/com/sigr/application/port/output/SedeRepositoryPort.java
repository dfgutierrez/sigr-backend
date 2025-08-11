package com.sigr.application.port.output;

import com.sigr.application.dto.dashboard.DashboardResponseDTO;
import com.sigr.domain.entity.Sede;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SedeRepositoryPort {
    
    List<Sede> findAll();
    
    Page<Sede> findAllPaginated(Pageable pageable);
    
    Optional<Sede> findById(Long id);
    
    Sede save(Sede sede);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByNombre(String nombre);
    
    List<Sede> findByNombreContaining(String nombre);
    
    long countVehiculosBySedeId(Long sedeId);
    
    long countInventarioBySedeId(Long sedeId);
    
    List<DashboardResponseDTO.ProductosPorSedeDTO> obtenerProductosPorSede();
}