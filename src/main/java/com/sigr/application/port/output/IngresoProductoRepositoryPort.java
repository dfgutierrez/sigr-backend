package com.sigr.application.port.output;

import com.sigr.domain.entity.IngresoProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IngresoProductoRepositoryPort {

    List<IngresoProducto> findAll();

    Page<IngresoProducto> findAllPaginated(Pageable pageable);

    Optional<IngresoProducto> findById(Long id);

    Optional<IngresoProducto> findByIdWithDetalles(Long id);

    List<IngresoProducto> findBySedeId(Long sedeId);

    List<IngresoProducto> findByUsuarioId(Long usuarioId);

    List<IngresoProducto> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<IngresoProducto> findBySedeIdAndFechaBetween(Long sedeId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    IngresoProducto save(IngresoProducto ingresoProducto);

    void deleteById(Long id);

    boolean existsById(Long id);
}