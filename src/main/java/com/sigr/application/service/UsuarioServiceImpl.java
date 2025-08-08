package com.sigr.application.service;

import com.sigr.application.dto.usuario.UsuarioRequestDTO;
import com.sigr.application.dto.usuario.UsuarioResponseDTO;
import com.sigr.application.dto.usuario.UsuarioUpdateDTO;
import com.sigr.application.mapper.UsuarioMapper;
import com.sigr.application.port.input.UsuarioUseCase;
import com.sigr.application.port.output.RolRepositoryPort;
import com.sigr.application.port.output.UsuarioRepositoryPort;
import com.sigr.domain.entity.Rol;
import com.sigr.domain.entity.Usuario;
import com.sigr.domain.entity.UsuarioRol;
import com.sigr.domain.exception.BusinessException;
import com.sigr.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RolRepositoryPort rolRepositoryPort;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Override
    public List<UsuarioResponseDTO> findAll() {
        log.debug("Finding all usuarios");
        List<Usuario> usuarios = usuarioRepositoryPort.findAllWithRoles();
        return usuarioMapper.toResponseDTOList(usuarios);
    }

    @Override
    public Page<UsuarioResponseDTO> findAllPaginated(Pageable pageable) {
        log.debug("Finding all usuarios paginated with page: {}, size: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        Page<Usuario> usuariosPage = usuarioRepositoryPort.findAllPaginated(pageable);
        return usuariosPage.map(usuarioMapper::toResponseDTO);
    }

    @Override
    public UsuarioResponseDTO findById(Long id) {
        log.debug("Finding usuario by id: {}", id);
        Usuario usuario = usuarioRepositoryPort.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO findByUsername(String username) {
        log.debug("Finding usuario by username: {}", username);
        Usuario usuario = usuarioRepositoryPort.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO create(UsuarioRequestDTO request) {
        log.debug("Creating new usuario: {}", request.getUsername());
        
        if (usuarioRepositoryPort.existsByUsername(request.getUsername())) {
            throw new BusinessException("Ya existe un usuario con el username: " + request.getUsername());
        }

        validateUsuarioRequest(request);
        
        Usuario usuario = usuarioMapper.toEntity(request);
        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        
        Usuario savedUsuario = usuarioRepositoryPort.save(usuario);
        
        // Manejar la foto si fue proporcionada
        if (request.getFoto() != null && !request.getFoto().isEmpty()) {
            try {
                String fotoUrl = saveUserPhoto(request.getFoto(), savedUsuario.getId());
                savedUsuario.setFotoUrl(fotoUrl);
                savedUsuario = usuarioRepositoryPort.save(savedUsuario);
            } catch (IOException e) {
                log.error("Error al guardar la foto del usuario: {}", e.getMessage());
                throw new BusinessException("Error al procesar la foto del usuario");
            }
        }
        
        // Asignar roles al usuario
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            assignRolesToUsuario(savedUsuario, request.getRoleIds());
        }
        
        log.info("Usuario created successfully with id: {}", savedUsuario.getId());
        return usuarioMapper.toResponseDTO(savedUsuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioUpdateDTO request) {
        log.debug("Updating usuario with id: {}", id);
        
        Usuario existingUsuario = usuarioRepositoryPort.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        if (!existingUsuario.getUsername().equals(request.getUsername()) && 
            usuarioRepositoryPort.existsByUsername(request.getUsername())) {
            throw new BusinessException("Ya existe un usuario con el username: " + request.getUsername());
        }

        validateUsuarioUpdateRequest(request);

        usuarioMapper.updateEntityFromUpdateDTO(request, existingUsuario);
        
        // Encriptar nueva contraseña si se proporciona
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            existingUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Eliminar roles existentes de la base de datos
        usuarioRepositoryPort.deleteUserRolesByUsuarioId(existingUsuario.getId());
        
        // Forzar la ejecución del DELETE antes de continuar
        entityManager.flush();
        
        // Limpiar la colección en memoria y asignar nuevos roles
        existingUsuario.getUsuarioRoles().clear();
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            assignRolesToUsuario(existingUsuario, request.getRoleIds());
        }
        
        Usuario updatedUsuario = usuarioRepositoryPort.save(existingUsuario);
        log.info("Usuario updated successfully with id: {}", updatedUsuario.getId());
        return usuarioMapper.toResponseDTO(updatedUsuario);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Soft deleting usuario with id: {}", id);
        
        Usuario usuario = usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        
        usuario.setEstado(false);
        usuarioRepositoryPort.save(usuario);
        log.info("Usuario soft deleted successfully with id: {}", id);
    }

    @Override
    public List<UsuarioResponseDTO> findByEstado(Boolean estado) {
        log.debug("Finding usuarios by estado: {}", estado);
        List<Usuario> usuarios = usuarioRepositoryPort.findByEstado(estado);
        return usuarioMapper.toResponseDTOList(usuarios);
    }

    @Override
    public List<UsuarioResponseDTO> findByNombreCompletoContaining(String nombreCompleto) {
        log.debug("Finding usuarios by nombre completo containing: {}", nombreCompleto);
        List<Usuario> usuarios = usuarioRepositoryPort.findByNombreCompletoContaining(nombreCompleto);
        return usuarioMapper.toResponseDTOList(usuarios);
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("Checking if usuario exists by username: {}", username);
        return usuarioRepositoryPort.existsByUsername(username);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String newPassword) {
        log.debug("Changing password for usuario with id: {}", id);
        
        Usuario usuario = usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new BusinessException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepositoryPort.save(usuario);
        
        log.info("Password changed successfully for usuario with id: {}", id);
    }

    @Override
    @Transactional
    public void toggleEstado(Long id) {
        log.debug("Toggling estado for usuario with id: {}", id);
        
        Usuario usuario = usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        
        usuario.setEstado(!usuario.getEstado());
        usuarioRepositoryPort.save(usuario);
        
        log.info("Estado toggled successfully for usuario with id: {}, new estado: {}", id, usuario.getEstado());
    }

    @Override
    public List<UsuarioResponseDTO> findByRolId(Long rolId) {
        log.debug("Finding usuarios by rol id: {}", rolId);
        List<Usuario> usuarios = usuarioRepositoryPort.findByRolId(rolId);
        return usuarioMapper.toResponseDTOList(usuarios);
    }

    private void validateUsuarioRequest(UsuarioRequestDTO request) {
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            List<Rol> existingRoles = rolRepositoryPort.findByIdIn(request.getRoleIds());
            if (existingRoles.size() != request.getRoleIds().size()) {
                throw new BusinessException("Uno o más roles no existen");
            }
        }
    }

    private void validateUsuarioUpdateRequest(UsuarioUpdateDTO request) {
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            List<Rol> existingRoles = rolRepositoryPort.findByIdIn(request.getRoleIds());
            if (existingRoles.size() != request.getRoleIds().size()) {
                throw new BusinessException("Uno o más roles no existen");
            }
        }
    }

    private void assignRolesToUsuario(Usuario usuario, List<Long> roleIds) {
        List<Rol> roles = rolRepositoryPort.findByIdIn(roleIds);
        
        for (Rol rol : roles) {
            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.setUsuario(usuario);
            usuarioRol.setRol(rol);
            usuario.getUsuarioRoles().add(usuarioRol);
        }
    }

    private String saveUserPhoto(MultipartFile foto, Long userId) throws IOException {
        String originalFilename = foto.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = "user_" + userId + "_" + UUID.randomUUID().toString() + fileExtension;
        
        Path uploadsPath = Paths.get("uploads/users");
        Files.createDirectories(uploadsPath);
        
        Path filePath = uploadsPath.resolve(fileName);
        Files.copy(foto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "/uploads/users/" + fileName;
    }
}