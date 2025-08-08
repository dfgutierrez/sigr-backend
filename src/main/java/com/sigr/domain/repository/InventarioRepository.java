package com.sigr.domain.repository;

import com.sigr.application.dto.reporte.ProductoStockBajoDTO;
import com.sigr.domain.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    Optional<Inventario> findByProductoIdAndSedeId(Long productoId, Long sedeId);
    
    List<Inventario> findBySedeId(Long sedeId);
    
    List<Inventario> findByProductoId(Long productoId);
    
    @Query("SELECT i FROM Inventario i WHERE i.cantidad <= :cantidad")
    List<Inventario> findByLowStock(@Param("cantidad") Integer cantidad);
    
    @Query("SELECT i FROM Inventario i WHERE i.sede.id = :sedeId AND i.cantidad <= :cantidad")
    List<Inventario> findBySedeIdAndLowStock(@Param("sedeId") Long sedeId, @Param("cantidad") Integer cantidad);
    
    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.sede.id = :sedeId")
    Integer countBySedeId(@Param("sedeId") Long sedeId);
    
    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.sede.id = :sedeId AND i.cantidad < 10")
    Integer countBySedeIdAndCantidadActualLessThanStockMinimo(@Param("sedeId") Long sedeId);
    
    @Query("SELECT SUM(i.cantidad * p.precioVenta) FROM Inventario i JOIN i.producto p WHERE i.sede.id = :sedeId")
    BigDecimal calcularValorTotalInventarioBySede(@Param("sedeId") Long sedeId);
    
    @Query("""
        SELECT new com.sigr.application.dto.reporte.ProductoStockBajoDTO(
            p.id, 
            p.nombre, 
            p.codigoBarra, 
            i.cantidad, 
            10, 
            p.precioVenta
        )
        FROM Inventario i 
        JOIN i.producto p 
        WHERE i.sede.id = :sedeId 
        AND i.cantidad < 10
        ORDER BY i.cantidad ASC
        """)
    List<ProductoStockBajoDTO> findProductosConStockBajo(@Param("sedeId") Long sedeId);
}