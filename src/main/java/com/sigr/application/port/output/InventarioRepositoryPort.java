package com.sigr.application.port.output;

import com.sigr.application.dto.dashboard.DashboardResponseDTO;
import com.sigr.application.dto.reporte.ProductoStockBajoDTO;
import com.sigr.domain.entity.Inventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InventarioRepositoryPort {

    List<Inventario> findAll();

    Page<Inventario> findAllPaginated(Pageable pageable);

    Optional<Inventario> findById(Long id);

    Optional<Inventario> findByProductoIdAndSedeId(Long productoId, Long sedeId);

    List<Inventario> findBySedeId(Long sedeId);

    List<Inventario> findByProductoId(Long productoId);

    List<Inventario> findByLowStock(Integer cantidad);

    List<Inventario> findBySedeIdAndLowStock(Long sedeId, Integer cantidad);

    Inventario save(Inventario inventario);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByProductoIdAndSedeId(Long productoId, Long sedeId);

    Integer countProductosBySede(Long sedeId);

    Integer countProductosConStockBajo(Long sedeId);

    BigDecimal calcularValorTotalInventario(Long sedeId);

    List<ProductoStockBajoDTO> findProductosConStockBajo(Long sedeId);
    
    DashboardResponseDTO.KpisDTO.InventarioDTO obtenerKpiInventario(Long sedeId);
}