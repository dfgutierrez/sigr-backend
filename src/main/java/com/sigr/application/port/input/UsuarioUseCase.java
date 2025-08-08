package com.sigr.application.port.input;

import com.sigr.application.dto.usuario.UsuarioRequestDTO;
import com.sigr.application.dto.usuario.UsuarioResponseDTO;
import com.sigr.application.dto.usuario.UsuarioUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsuarioUseCase {
    
    List<UsuarioResponseDTO> findAll();
    
    Page<UsuarioResponseDTO> findAllPaginated(Pageable pageable);
    
    UsuarioResponseDTO findById(Long id);
    
    UsuarioResponseDTO findByUsername(String username);
    
    UsuarioResponseDTO create(UsuarioRequestDTO request);
    
    UsuarioResponseDTO update(Long id, UsuarioUpdateDTO request);
    
    void deleteById(Long id);
    
    List<UsuarioResponseDTO> findByEstado(Boolean estado);
    
    List<UsuarioResponseDTO> findByNombreCompletoContaining(String nombreCompleto);
    
    boolean existsByUsername(String username);
    
    void changePassword(Long id, String newPassword);
    
    void toggleEstado(Long id);
    
    List<UsuarioResponseDTO> findByRolId(Long rolId);
}