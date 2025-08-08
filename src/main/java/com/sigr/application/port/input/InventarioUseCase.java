package com.sigr.application.port.input;

import com.sigr.application.dto.inventario.InventarioRequestDTO;
import com.sigr.application.dto.inventario.InventarioResponseDTO;
import com.sigr.application.dto.inventario.InventarioUpdateDTO;
import com.sigr.application.dto.inventario.StockAdjustmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventarioUseCase {

    List<InventarioResponseDTO> findAll();

    Page<InventarioResponseDTO> findAllPaginated(Pageable pageable);

    InventarioResponseDTO findById(Long id);

    InventarioResponseDTO findByProductoAndSede(Long productoId, Long sedeId);

    List<InventarioResponseDTO> findBySedeId(Long sedeId);

    List<InventarioResponseDTO> findByProductoId(Long productoId);

    List<InventarioResponseDTO> findLowStock(Integer cantidad);

    List<InventarioResponseDTO> findLowStockBySede(Long sedeId, Integer cantidad);

    InventarioResponseDTO create(InventarioRequestDTO request);

    InventarioResponseDTO update(Long id, InventarioUpdateDTO request);

    InventarioResponseDTO adjustStock(Long id, Integer cantidadAjuste, String motivo);

    void deleteById(Long id);

    boolean existsByProductoAndSede(Long productoId, Long sedeId);
    
    InventarioResponseDTO adjustStock(Long id, StockAdjustmentDTO request);
}