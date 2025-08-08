package com.sigr.domain.repository;

import com.sigr.domain.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    List<Rol> findByIdIn(List<Long> ids);
    
    boolean existsByNombre(String nombre);
    
    List<Rol> findByNombreContaining(String nombre);
    
    @Query("SELECT COUNT(ur) FROM UsuarioRol ur WHERE ur.rol.id = :rolId")
    long countUsuariosByRolId(@Param("rolId") Long rolId);
    
    @Query("SELECT COUNT(m) FROM Menu m")
    long countMenusByRolId(@Param("rolId") Long rolId);
}