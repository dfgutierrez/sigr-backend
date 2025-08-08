package com.sigr.application.port.input;

import com.sigr.application.dto.rol.RolRequestDTO;
import com.sigr.application.dto.rol.RolResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RolUseCase {
    
    List<RolResponseDTO> findAll();
    
    Page<RolResponseDTO> findAllPaginated(Pageable pageable);
    
    RolResponseDTO findById(Long id);
    
    RolResponseDTO create(RolRequestDTO request);
    
    RolResponseDTO update(Long id, RolRequestDTO request);
    
    void deleteById(Long id);
    
    boolean existsByNombre(String nombre);
    
    List<RolResponseDTO> findByNombreContaining(String nombre);
}