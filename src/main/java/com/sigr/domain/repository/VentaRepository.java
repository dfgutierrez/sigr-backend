package com.sigr.domain.repository;

import com.sigr.domain.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findBySedeId(Long sedeId);
    
    List<Venta> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT v FROM Venta v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                 @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT v FROM Venta v WHERE v.sede.id = :sedeId AND v.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findBySedeIdAndFechaBetween(@Param("sedeId") Long sedeId,
                                          @Param("fechaInicio") LocalDateTime fechaInicio,
                                          @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.detalles WHERE v.id = :id")
    Venta findByIdWithDetalles(@Param("id") Long id);
    
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.sede.id = :sedeId AND v.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal getTotalVentasBySedeAndFecha(@Param("sedeId") Long sedeId,
                                           @Param("fechaInicio") LocalDateTime fechaInicio,
                                           @Param("fechaFin") LocalDateTime fechaFin);
}