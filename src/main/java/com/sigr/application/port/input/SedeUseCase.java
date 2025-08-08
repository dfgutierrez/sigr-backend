package com.sigr.application.port.input;

import com.sigr.application.dto.sede.SedeRequestDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SedeUseCase {
    
    List<SedeResponseDTO> findAll();
    
    Page<SedeResponseDTO> findAllPaginated(Pageable pageable);
    
    SedeResponseDTO findById(Long id);
    
    SedeResponseDTO create(SedeRequestDTO request);
    
    SedeResponseDTO update(Long id, SedeRequestDTO request);
    
    void deleteById(Long id);
    
    boolean existsByNombre(String nombre);
    
    List<SedeResponseDTO> findByNombreContaining(String nombre);
}