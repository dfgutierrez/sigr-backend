package com.sigr.infrastructure.controller;

import com.sigr.application.dto.usuario.ChangePasswordDTO;
import com.sigr.application.dto.usuario.UsuarioRequestDTO;
import com.sigr.application.dto.usuario.UsuarioResponseDTO;
import com.sigr.application.dto.usuario.UsuarioUpdateDTO;
import com.sigr.application.port.input.UsuarioUseCase;
import com.sigr.infrastructure.common.response.ApiResponse;
import com.sigr.infrastructure.common.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
@SecurityRequirement(name = "Bearer Authentication")
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios registrados")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> getAllUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener usuarios paginados", description = "Retorna una página de usuarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<PagedResponse<UsuarioResponseDTO>>> getAllUsuariosPaginated(Pageable pageable) {
        Page<UsuarioResponseDTO> usuariosPage = usuarioUseCase.findAllPaginated(pageable);
        PagedResponse<UsuarioResponseDTO> pagedResponse = PagedResponse.of(usuariosPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario específico por su ID")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> getUsuarioById(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Obtener usuario por username", description = "Retorna un usuario específico por su username")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> getUsuarioByUsername(
            @Parameter(description = "Username del usuario") @PathVariable String username) {
        UsuarioResponseDTO usuario = usuarioUseCase.findByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema con foto opcional")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> createUsuario(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "nombreCompleto", required = false) String nombreCompleto,
            @RequestParam("estado") Boolean estado,
            @RequestParam(value = "sedeId", required = false) Long sedeId,
            @RequestParam("roleIds") String roleIds,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {
        
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setUsername(username);
        request.setPassword(password);
        request.setNombreCompleto(nombreCompleto);
        request.setEstado(estado);
        request.setSedeId(sedeId);
        request.setRoleIds(parseIds(roleIds));
        request.setFoto(foto);
        
        UsuarioResponseDTO usuario = usuarioUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(usuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario (JSON)", description = "Actualiza un usuario existente usando JSON")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> updateUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO request) {
        UsuarioResponseDTO usuario = usuarioUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @PutMapping(value = "/{id}/with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar usuario con archivo", description = "Actualiza un usuario existente incluyendo foto de perfil")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> updateUsuarioWithFile(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "nombreCompleto", required = false) String nombreCompleto,
            @RequestParam(value = "estado", required = false) Boolean estado,
            @RequestParam(value = "sedeId", required = false) Long sedeId,
            @RequestParam(value = "roleIds", required = false) String roleIds,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {
        
        UsuarioUpdateDTO request = new UsuarioUpdateDTO();
        if (username != null) request.setUsername(username);
        if (password != null) request.setPassword(password);
        if (nombreCompleto != null) request.setNombreCompleto(nombreCompleto);
        if (estado != null) request.setEstado(estado);
        if (sedeId != null) request.setSedeId(sedeId);
        if (roleIds != null) request.setRoleIds(parseIds(roleIds));
        if (foto != null) request.setFoto(foto);
        
        UsuarioResponseDTO usuario = usuarioUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> deleteUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        usuarioUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener usuarios por estado", description = "Retorna usuarios filtrados por estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> getUsuariosByEstado(
            @Parameter(description = "Estado del usuario (true/false)") @PathVariable Boolean estado) {
        List<UsuarioResponseDTO> usuarios = usuarioUseCase.findByEstado(estado);
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar usuarios por nombre", description = "Busca usuarios que contengan el texto especificado en el nombre completo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> searchUsuariosByNombre(
            @Parameter(description = "Texto a buscar en el nombre completo") @RequestParam String nombre) {
        List<UsuarioResponseDTO> usuarios = usuarioUseCase.findByNombreCompletoContaining(nombre);
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @GetMapping("/exists")
    @Operation(summary = "Verificar si existe un usuario", description = "Verifica si existe un usuario con el username especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsByUsername(
            @Parameter(description = "Username del usuario") @RequestParam String username) {
        boolean exists = usuarioUseCase.existsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @PutMapping("/{id}/change-password")
    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña de un usuario")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDTO request) {
        usuarioUseCase.changePassword(id, request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}/toggle-estado")
    @Operation(summary = "Cambiar estado del usuario", description = "Activa o desactiva un usuario")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> toggleEstado(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        usuarioUseCase.toggleEstado(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/rol/{rolId}")
    @Operation(summary = "Obtener usuarios por rol", description = "Retorna usuarios que tienen un rol específico")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> getUsuariosByRol(
            @Parameter(description = "ID del rol") @PathVariable Long rolId) {
        List<UsuarioResponseDTO> usuarios = usuarioUseCase.findByRolId(rolId);
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @PostMapping("/{id}/upload-photo")
    @Operation(summary = "Subir foto de usuario", description = "Sube una foto para el usuario y retorna la URL")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<String>> uploadUserPhoto(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Parameter(description = "Archivo de imagen") @RequestParam("photo") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("No se ha seleccionado ningún archivo"));
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "user_" + id + "_" + UUID.randomUUID().toString() + fileExtension;
            
            Path uploadsPath = Paths.get("uploads/users");
            Files.createDirectories(uploadsPath);
            
            Path filePath = uploadsPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String photoUrl = "/uploads/users/" + fileName;
            
            return ResponseEntity.ok(ApiResponse.success(photoUrl));
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al subir el archivo: " + e.getMessage()));
        }
    }

    private List<Long> parseIds(String idsString) {
        if (idsString == null || idsString.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(idsString.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}