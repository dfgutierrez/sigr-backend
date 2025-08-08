package com.sigr.infrastructure.adapter.output;

import com.sigr.application.dto.reporte.ReporteUsuariosDTO;
import com.sigr.application.port.output.UsuarioRepositoryPort;
import com.sigr.domain.entity.Usuario;
import com.sigr.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Page<Usuario> findAllPaginated(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByIdWithRoles(Long id) {
        return usuarioRepository.findByIdWithRoles(id);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Optional<Usuario> findByUsernameWithRoles(String username) {
        return usuarioRepository.findByUsernameWithRoles(username);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    public List<Usuario> findByEstado(Boolean estado) {
        return usuarioRepository.findByEstado(estado);
    }

    @Override
    public List<Usuario> findByNombreCompletoContaining(String nombreCompleto) {
        return usuarioRepository.findByNombreCompletoContainingIgnoreCase(nombreCompleto);
    }

    @Override
    public List<Usuario> findAllWithRoles() {
        return usuarioRepository.findAllWithRoles();
    }

    @Override
    public List<Usuario> findByRolId(Long rolId) {
        return usuarioRepository.findByRolId(rolId);
    }

    @Override
    public List<ReporteUsuariosDTO> findReporteRendimientoUsuarios(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId) {
        return usuarioRepository.findReporteRendimientoUsuarios(fechaInicio, fechaFin, sedeId);
    }

    @Override
    public List<Usuario> findActiveBySedeId(Long sedeId) {
        return usuarioRepository.findActiveBySedeId(sedeId);
    }
    
    @Override
    public void deleteUserRolesByUsuarioId(Long usuarioId) {
        usuarioRepository.deleteUserRolesByUsuarioId(usuarioId);
    }
}