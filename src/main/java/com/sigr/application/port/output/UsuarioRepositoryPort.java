package com.sigr.application.port.output;

import com.sigr.application.dto.reporte.ReporteUsuariosDTO;
import com.sigr.domain.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    
    List<Usuario> findAll();
    
    Page<Usuario> findAllPaginated(Pageable pageable);
    
    Optional<Usuario> findById(Long id);
    
    Optional<Usuario> findByIdWithRoles(Long id);
    
    Optional<Usuario> findByUsername(String username);
    
    Optional<Usuario> findByUsernameWithRoles(String username);
    
    Usuario save(Usuario usuario);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByUsername(String username);
    
    List<Usuario> findByEstado(Boolean estado);
    
    List<Usuario> findByNombreCompletoContaining(String nombreCompleto);
    
    List<Usuario> findAllWithRoles();
    
    List<Usuario> findByRolId(Long rolId);
    
    List<ReporteUsuariosDTO> findReporteRendimientoUsuarios(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId);
    
    List<Usuario> findActiveBySedeId(Long sedeId);
    
    void deleteUserRolesByUsuarioId(Long usuarioId);
}