package com.sigr.domain.repository;

import com.sigr.domain.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {
    
    boolean existsByNombre(String nombre);
    
    List<Sede> findByNombreContaining(String nombre);
    
    @Query("SELECT COUNT(v) FROM Vehiculo v WHERE v.sede.id = :sedeId")
    long countVehiculosBySedeId(@Param("sedeId") Long sedeId);
    
    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.sede.id = :sedeId")
    long countInventarioBySedeId(@Param("sedeId") Long sedeId);
}