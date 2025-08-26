package com.sigr.application.port.input;

import com.sigr.application.dto.producto.ProductoRequestDTO;
import com.sigr.application.dto.producto.ProductoResponseDTO;
import com.sigr.application.dto.producto.ProductoUpdateDTO;
import com.sigr.application.dto.producto.ProductoConStockDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductoUseCase {

    List<ProductoResponseDTO> findAll();

    List<ProductoResponseDTO> findAll(Long sedeId);

    Page<ProductoResponseDTO> findAllPaginated(Pageable pageable);

    ProductoResponseDTO findById(Long id);

    ProductoResponseDTO findByCodigoBarra(String codigoBarra);

    List<ProductoResponseDTO> findByNombreContaining(String nombre);

    List<ProductoResponseDTO> findByCategoriaId(Long categoriaId);

    ProductoResponseDTO create(ProductoRequestDTO request);

    ProductoResponseDTO update(Long id, ProductoUpdateDTO request);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByCodigoBarra(String codigoBarra);
    
    List<ProductoConStockDTO> findBySedeWithStock(Long sedeId);
    
    List<ProductoConStockDTO> findBySedeWithStock(Long sedeId, Boolean soloConStock);
}