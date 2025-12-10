package com.sigr.application.port.input;

import com.sigr.application.dto.proveedor.ProveedorRequestDTO;
import com.sigr.application.dto.proveedor.ProveedorResponseDTO;
import com.sigr.application.dto.proveedor.ProveedorUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProveedorUseCase {

    List<ProveedorResponseDTO> findAll();

    Page<ProveedorResponseDTO> findAllPaginated(Pageable pageable);

    ProveedorResponseDTO findById(Long id);

    List<ProveedorResponseDTO> findByNombreContaining(String nombre);

    ProveedorResponseDTO create(ProveedorRequestDTO request);

    ProveedorResponseDTO update(Long id, ProveedorUpdateDTO request);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByNombre(String nombre);

    long countProductosByProveedorId(Long proveedorId);
}