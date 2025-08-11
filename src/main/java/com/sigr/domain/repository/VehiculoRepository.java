package com.sigr.domain.repository;

import com.sigr.domain.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    
    boolean existsByPlaca(String placa);
    
    List<Vehiculo> findByTipo(String tipo);
    
    List<Vehiculo> findByPlacaContaining(String placa);
    
    List<Vehiculo> findByEstado(Boolean estado);
    
    List<Vehiculo> findBySedeId(Long sedeId);

    @Query("SELECT COUNT(v) FROM Vehiculo v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    Long countVehiculosNuevosEnPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                       @Param("fechaFin") LocalDateTime fechaFin);
}