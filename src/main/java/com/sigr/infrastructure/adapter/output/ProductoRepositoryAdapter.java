package com.sigr.infrastructure.adapter.output;

import com.sigr.application.port.output.ProductoRepositoryPort;
import com.sigr.application.dto.producto.ProductoConStockDTO;
import com.sigr.domain.entity.Producto;
import com.sigr.domain.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final ProductoRepository productoRepository;

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Page<Producto> findAllPaginated(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Optional<Producto> findByCodigoBarra(String codigoBarra) {
        return productoRepository.findByCodigoBarra(codigoBarra);
    }

    @Override
    public List<Producto> findByNombreContaining(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Producto> findByCategoriaId(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productoRepository.existsById(id);
    }

    @Override
    public boolean existsByCodigoBarra(String codigoBarra) {
        return productoRepository.existsByCodigoBarra(codigoBarra);
    }

    @Override
    public List<ProductoConStockDTO> findProductosBySedeWithStock(Long sedeId) {
        return productoRepository.findProductosBySedeWithStock(sedeId);
    }

    @Override
    public List<ProductoConStockDTO> findProductosBySedeWithStock(Long sedeId, Boolean soloConStock) {
        return productoRepository.findProductosBySedeWithStock(sedeId, soloConStock);
    }
}