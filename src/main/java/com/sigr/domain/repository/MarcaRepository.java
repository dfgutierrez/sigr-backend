package com.sigr.domain.repository;

import com.sigr.domain.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    
    List<Marca> findByNombreContainingIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCase(String nombre);
    
    @Query("SELECT COUNT(v) FROM Vehiculo v WHERE v.marca.id = :marcaId")
    long countVehiculosByMarcaId(@Param("marcaId") Long marcaId);
}