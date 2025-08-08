package com.sigr.application.port.input;

import com.sigr.application.dto.ingreso.IngresoProductoRequestDTO;
import com.sigr.application.dto.ingreso.IngresoProductoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IngresoProductoUseCase {

    List<IngresoProductoResponseDTO> findAll();

    Page<IngresoProductoResponseDTO> findAllPaginated(Pageable pageable);

    IngresoProductoResponseDTO findById(Long id);

    List<IngresoProductoResponseDTO> findBySedeId(Long sedeId);

    List<IngresoProductoResponseDTO> findByUsuarioId(Long usuarioId);

    List<IngresoProductoResponseDTO> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<IngresoProductoResponseDTO> findBySedeIdAndFechaBetween(Long sedeId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    IngresoProductoResponseDTO create(IngresoProductoRequestDTO request);

    void deleteById(Long id);
}