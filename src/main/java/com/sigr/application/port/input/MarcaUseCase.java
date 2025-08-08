package com.sigr.application.port.input;

import com.sigr.application.dto.marca.MarcaRequestDTO;
import com.sigr.application.dto.marca.MarcaResponseDTO;
import com.sigr.application.dto.marca.MarcaUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MarcaUseCase {

    List<MarcaResponseDTO> findAll();

    Page<MarcaResponseDTO> findAllPaginated(Pageable pageable);

    MarcaResponseDTO findById(Long id);

    List<MarcaResponseDTO> findByNombreContaining(String nombre);

    MarcaResponseDTO create(MarcaRequestDTO request);

    MarcaResponseDTO update(Long id, MarcaUpdateDTO request);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByNombre(String nombre);

    long countVehiculosByMarcaId(Long marcaId);
}