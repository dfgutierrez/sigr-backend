package com.sigr.application.port.input;

import com.sigr.application.dto.categoria.CategoriaRequestDTO;
import com.sigr.application.dto.categoria.CategoriaResponseDTO;
import com.sigr.application.dto.categoria.CategoriaUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriaUseCase {

    List<CategoriaResponseDTO> findAll();

    Page<CategoriaResponseDTO> findAllPaginated(Pageable pageable);

    CategoriaResponseDTO findById(Long id);

    List<CategoriaResponseDTO> findByNombreContaining(String nombre);

    CategoriaResponseDTO create(CategoriaRequestDTO request);

    CategoriaResponseDTO update(Long id, CategoriaUpdateDTO request);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByNombre(String nombre);

    long countProductosByCategoriaId(Long categoriaId);
}