package com.sigr.domain.repository;

import com.sigr.domain.entity.IngresoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngresoProductoRepository extends JpaRepository<IngresoProducto, Long> {
    
    List<IngresoProducto> findBySedeId(Long sedeId);
    
    List<IngresoProducto> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT i FROM IngresoProducto i WHERE i.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<IngresoProducto> findByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                           @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT i FROM IngresoProducto i WHERE i.sede.id = :sedeId AND i.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<IngresoProducto> findBySedeIdAndFechaBetween(@Param("sedeId") Long sedeId,
                                                    @Param("fechaInicio") LocalDateTime fechaInicio,
                                                    @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT i FROM IngresoProducto i LEFT JOIN FETCH i.detalles WHERE i.id = :id")
    Optional<IngresoProducto> findByIdWithDetalles(@Param("id") Long id);
}