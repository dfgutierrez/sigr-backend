package com.sigr.application.port.output;

import com.sigr.domain.entity.Producto;
import com.sigr.application.dto.producto.ProductoConStockDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {

    List<Producto> findAll();

    Page<Producto> findAllPaginated(Pageable pageable);

    Optional<Producto> findById(Long id);

    Optional<Producto> findByCodigoBarra(String codigoBarra);

    List<Producto> findByNombreContaining(String nombre);

    List<Producto> findByCategoriaId(Long categoriaId);

    Producto save(Producto producto);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByCodigoBarra(String codigoBarra);
    
    List<ProductoConStockDTO> findProductosBySedeWithStock(Long sedeId);
    
    List<ProductoConStockDTO> findProductosBySedeWithStock(Long sedeId, Boolean soloConStock);
}