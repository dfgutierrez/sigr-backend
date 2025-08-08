package com.sigr.infrastructure.controller;

import com.sigr.application.dto.rol.RolRequestDTO;
import com.sigr.application.dto.rol.RolResponseDTO;
import com.sigr.application.port.input.RolUseCase;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "API para gestión de roles del sistema")
@SecurityRequirement(name = "Bearer Authentication")
public class RolController {

    private final RolUseCase rolUseCase;

    @GetMapping
    @Operation(summary = "Obtener todos los roles", description = "Retorna una lista de todos los roles registrados")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<RolResponseDTO>>> getAllRoles() {
        List<RolResponseDTO> roles = rolUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener roles paginados", description = "Retorna una página de roles")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<PagedResponse<RolResponseDTO>>> getAllRolesPaginated(Pageable pageable) {
        Page<RolResponseDTO> rolesPage = rolUseCase.findAllPaginated(pageable);
        PagedResponse<RolResponseDTO> pagedResponse = PagedResponse.of(rolesPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID", description = "Retorna un rol específico por su ID")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<RolResponseDTO>> getRolById(
            @Parameter(description = "ID del rol") @PathVariable Long id) {
        RolResponseDTO rol = rolUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(rol));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo rol", description = "Crea un nuevo rol en el sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<RolResponseDTO>> createRol(
            @Valid @RequestBody RolRequestDTO request) {
        RolResponseDTO rol = rolUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(rol));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar rol", description = "Actualiza un rol existente")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<RolResponseDTO>> updateRol(
            @Parameter(description = "ID del rol") @PathVariable Long id,
            @Valid @RequestBody RolRequestDTO request) {
        RolResponseDTO rol = rolUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(rol));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rol", description = "Elimina un rol del sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> deleteRol(
            @Parameter(description = "ID del rol") @PathVariable Long id) {
        rolUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/exists")
    @Operation(summary = "Verificar si existe un rol", description = "Verifica si existe un rol con el nombre especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsByNombre(
            @Parameter(description = "Nombre del rol") @RequestParam String nombre) {
        boolean exists = rolUseCase.existsByNombre(nombre);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar roles por nombre", description = "Busca roles que contengan el texto especificado en el nombre")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<RolResponseDTO>>> searchRolesByNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre) {
        List<RolResponseDTO> roles = rolUseCase.findByNombreContaining(nombre);
        return ResponseEntity.ok(ApiResponse.success(roles));
    }
}