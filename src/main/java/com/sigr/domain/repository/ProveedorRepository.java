package com.sigr.domain.repository;

import com.sigr.domain.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCase(String nombre);
    
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.proveedor.id = :proveedorId")
    long countProductosByProveedorId(@Param("proveedorId") Long proveedorId);
}