package com.sigr.domain.repository;

import com.sigr.application.dto.producto.ProductoConStockDTO;
import com.sigr.domain.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoBarra(String codigoBarra);

    boolean existsByCodigoBarra(String codigoBarra);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoriaId(Long categoriaId);

    @Query("""
        SELECT new com.sigr.application.dto.producto.ProductoConStockDTO(
            p.id, p.codigoBarra, p.nombre, p.descripcion, p.precioCompra, p.precioVenta,
            c.id, c.nombre,
            i.id, i.cantidad, s.id, s.nombre,
            CASE WHEN i.cantidad > 0 THEN true ELSE false END,
            CASE WHEN i.cantidad <= 5 THEN true ELSE false END
        )
        FROM Producto p
        LEFT JOIN p.categoria c
        LEFT JOIN Inventario i ON p.id = i.producto.id
        LEFT JOIN i.sede s
        WHERE s.id = :sedeId
        ORDER BY p.nombre ASC
    """)
    List<ProductoConStockDTO> findProductosBySedeWithStock(@Param("sedeId") Long sedeId);

    @Query("""
        SELECT new com.sigr.application.dto.producto.ProductoConStockDTO(
            p.id, p.codigoBarra, p.nombre, p.descripcion, p.precioCompra, p.precioVenta,
            c.id, c.nombre,
            i.id, i.cantidad, s.id, s.nombre,
            CASE WHEN i.cantidad > 0 THEN true ELSE false END,
            CASE WHEN i.cantidad <= 5 THEN true ELSE false END
        )
        FROM Producto p
        LEFT JOIN p.categoria c
        LEFT JOIN Inventario i ON p.id = i.producto.id
        LEFT JOIN i.sede s
        WHERE s.id = :sedeId
        AND (:soloConStock = false OR i.cantidad > 0)
        ORDER BY p.nombre ASC
    """)
    List<ProductoConStockDTO> findProductosBySedeWithStock(@Param("sedeId") Long sedeId, @Param("soloConStock") Boolean soloConStock);
}