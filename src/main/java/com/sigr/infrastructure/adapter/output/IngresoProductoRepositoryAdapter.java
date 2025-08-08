package com.sigr.infrastructure.adapter.output;

import com.sigr.application.port.output.IngresoProductoRepositoryPort;
import com.sigr.domain.entity.IngresoProducto;
import com.sigr.domain.repository.IngresoProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IngresoProductoRepositoryAdapter implements IngresoProductoRepositoryPort {

    private final IngresoProductoRepository ingresoProductoRepository;

    @Override
    public List<IngresoProducto> findAll() {
        return ingresoProductoRepository.findAll();
    }

    @Override
    public Page<IngresoProducto> findAllPaginated(Pageable pageable) {
        return ingresoProductoRepository.findAll(pageable);
    }

    @Override
    public Optional<IngresoProducto> findById(Long id) {
        return ingresoProductoRepository.findById(id);
    }

    @Override
    public Optional<IngresoProducto> findByIdWithDetalles(Long id) {
        return ingresoProductoRepository.findByIdWithDetalles(id);
    }

    @Override
    public List<IngresoProducto> findBySedeId(Long sedeId) {
        return ingresoProductoRepository.findBySedeId(sedeId);
    }

    @Override
    public List<IngresoProducto> findByUsuarioId(Long usuarioId) {
        return ingresoProductoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<IngresoProducto> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ingresoProductoRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Override
    public List<IngresoProducto> findBySedeIdAndFechaBetween(Long sedeId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ingresoProductoRepository.findBySedeIdAndFechaBetween(sedeId, fechaInicio, fechaFin);
    }

    @Override
    public IngresoProducto save(IngresoProducto ingresoProducto) {
        return ingresoProductoRepository.save(ingresoProducto);
    }

    @Override
    public void deleteById(Long id) {
        ingresoProductoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return ingresoProductoRepository.existsById(id);
    }
}