package com.sigr.infrastructure.adapter.output;

import com.sigr.application.dto.reporte.ProductoStockBajoDTO;
import com.sigr.application.port.output.InventarioRepositoryPort;
import com.sigr.domain.entity.Inventario;
import com.sigr.domain.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InventarioRepositoryAdapter implements InventarioRepositoryPort {

    private final InventarioRepository inventarioRepository;

    @Override
    public List<Inventario> findAll() {
        return inventarioRepository.findAll();
    }

    @Override
    public Page<Inventario> findAllPaginated(Pageable pageable) {
        return inventarioRepository.findAll(pageable);
    }

    @Override
    public Optional<Inventario> findById(Long id) {
        return inventarioRepository.findById(id);
    }

    @Override
    public Optional<Inventario> findByProductoIdAndSedeId(Long productoId, Long sedeId) {
        return inventarioRepository.findByProductoIdAndSedeId(productoId, sedeId);
    }

    @Override
    public List<Inventario> findBySedeId(Long sedeId) {
        return inventarioRepository.findBySedeId(sedeId);
    }

    @Override
    public List<Inventario> findByProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    @Override
    public List<Inventario> findByLowStock(Integer cantidad) {
        return inventarioRepository.findByLowStock(cantidad);
    }

    @Override
    public List<Inventario> findBySedeIdAndLowStock(Long sedeId, Integer cantidad) {
        return inventarioRepository.findBySedeIdAndLowStock(sedeId, cantidad);
    }

    @Override
    public Inventario save(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    @Override
    public void deleteById(Long id) {
        inventarioRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return inventarioRepository.existsById(id);
    }

    @Override
    public boolean existsByProductoIdAndSedeId(Long productoId, Long sedeId) {
        return inventarioRepository.findByProductoIdAndSedeId(productoId, sedeId).isPresent();
    }

    @Override
    public Integer countProductosBySede(Long sedeId) {
        return inventarioRepository.countBySedeId(sedeId);
    }

    @Override
    public Integer countProductosConStockBajo(Long sedeId) {
        return inventarioRepository.countBySedeIdAndCantidadActualLessThanStockMinimo(sedeId);
    }

    @Override
    public BigDecimal calcularValorTotalInventario(Long sedeId) {
        return inventarioRepository.calcularValorTotalInventarioBySede(sedeId);
    }

    @Override
    public List<ProductoStockBajoDTO> findProductosConStockBajo(Long sedeId) {
        return inventarioRepository.findProductosConStockBajo(sedeId);
    }
}