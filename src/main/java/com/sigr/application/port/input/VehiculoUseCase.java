package com.sigr.application.port.input;

import com.sigr.application.dto.vehiculo.VehiculoRequestDTO;
import com.sigr.application.dto.vehiculo.VehiculoResponseDTO;
import com.sigr.application.dto.vehiculo.VehiculoUpdateDTO;
import com.sigr.application.dto.vehiculo.VehiculoSearchResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehiculoUseCase {
    
    List<VehiculoResponseDTO> findAll();
    
    Page<VehiculoResponseDTO> findAllPaginated(Pageable pageable);
    
    VehiculoResponseDTO findById(Long id);
    
    VehiculoResponseDTO create(VehiculoRequestDTO request);
    
    VehiculoResponseDTO update(Long id, VehiculoUpdateDTO request);
    
    void deleteById(Long id);
    
    List<VehiculoResponseDTO> findByTipo(String tipo);
    
    List<VehiculoResponseDTO> findByPlacaContaining(String placa);
    
    List<VehiculoResponseDTO> findByEstado(Boolean estado);
    
    List<VehiculoResponseDTO> findBySede(Long sedeId);
    
    VehiculoSearchResultDTO searchByPlaca(String placa);
    
    VehiculoResponseDTO findByPlaca(String placa);
}