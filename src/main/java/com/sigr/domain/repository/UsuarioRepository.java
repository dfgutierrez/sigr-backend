package com.sigr.domain.repository;

import com.sigr.application.dto.reporte.ReporteUsuariosDTO;
import com.sigr.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM Usuario u " +
           "LEFT JOIN FETCH u.usuarioRoles ur " +
           "LEFT JOIN FETCH ur.rol " +
           "LEFT JOIN FETCH u.sede " +
           "WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithRoles(@Param("username") String username);
    
    @Query("SELECT u FROM Usuario u " +
           "LEFT JOIN FETCH u.usuarioRoles ur " +
           "LEFT JOIN FETCH ur.rol " +
           "LEFT JOIN FETCH u.sede " +
           "WHERE u.id = :id")
    Optional<Usuario> findByIdWithRoles(@Param("id") Long id);
    
    @Query("SELECT u FROM Usuario u " +
           "LEFT JOIN FETCH u.usuarioRoles ur " +
           "LEFT JOIN FETCH ur.rol " +
           "LEFT JOIN FETCH u.sede")
    List<Usuario> findAllWithRoles();
    
    List<Usuario> findByEstado(Boolean estado);
    
    List<Usuario> findByNombreCompletoContainingIgnoreCase(String nombreCompleto);
    
    @Query("SELECT u FROM Usuario u " +
           "JOIN u.usuarioRoles ur " +
           "WHERE ur.rol.id = :rolId")
    List<Usuario> findByRolId(@Param("rolId") Long rolId);
    
    List<Usuario> findBySedeId(Long sedeId);
    
    @Query("SELECT u FROM Usuario u WHERE u.sede.id = :sedeId AND u.estado = true")
    List<Usuario> findActiveBySedeId(@Param("sedeId") Long sedeId);
    
    @Query("""
        SELECT new com.sigr.application.dto.reporte.ReporteUsuariosDTO(
            u.id, 
            u.nombreCompleto, 
            s.nombre, 
            :fechaInicio, 
            :fechaFin, 
            COUNT(v.id), 
            COALESCE(SUM(v.total), 0), 
            COALESCE(AVG(v.total), 0),
            1
        )
        FROM Usuario u 
        LEFT JOIN u.sede s
        LEFT JOIN Venta v ON u.id = v.usuario.id 
            AND v.fecha BETWEEN :fechaInicio AND :fechaFin 
            AND v.estado = true
        WHERE (:sedeId IS NULL OR u.sede.id = :sedeId)
        AND u.estado = true
        GROUP BY u.id, u.nombreCompleto, s.nombre
        ORDER BY COALESCE(SUM(v.total), 0) DESC
        """)
    List<ReporteUsuariosDTO> findReporteRendimientoUsuarios(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                            @Param("fechaFin") LocalDateTime fechaFin,
                                                            @Param("sedeId") Long sedeId);
    
    @Modifying
    @Query("DELETE FROM UsuarioRol ur WHERE ur.usuario.id = :usuarioId")
    void deleteUserRolesByUsuarioId(@Param("usuarioId") Long usuarioId);
}