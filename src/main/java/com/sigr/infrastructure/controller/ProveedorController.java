package com.sigr.infrastructure.controller;

import com.sigr.application.dto.proveedor.ProveedorRequestDTO;
import com.sigr.application.dto.proveedor.ProveedorResponseDTO;
import com.sigr.application.dto.proveedor.ProveedorUpdateDTO;
import com.sigr.application.port.input.ProveedorUseCase;
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
@RequestMapping("/api/v1/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "API para gestión de proveedores")
@SecurityRequirement(name = "Bearer Authentication")
public class ProveedorController {

    private final ProveedorUseCase proveedorUseCase;

    @GetMapping
    @Operation(summary = "Obtener todos los proveedores", description = "Retorna una lista de todos los proveedores")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ProveedorResponseDTO>>> getAllProveedores() {
        List<ProveedorResponseDTO> proveedores = proveedorUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(proveedores));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener proveedores paginados", description = "Retorna una página de proveedores")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<PagedResponse<ProveedorResponseDTO>>> getAllProveedoresPaginated(Pageable pageable) {
        Page<ProveedorResponseDTO> proveedoresPage = proveedorUseCase.findAllPaginated(pageable);
        PagedResponse<ProveedorResponseDTO> pagedResponse = PagedResponse.of(proveedoresPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID", description = "Retorna un proveedor específico por su ID")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> getProveedorById(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        ProveedorResponseDTO proveedor = proveedorUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(proveedor));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar proveedores por nombre", description = "Busca proveedores que contengan el texto especificado en el nombre")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ProveedorResponseDTO>>> searchProveedoresByNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre) {
        List<ProveedorResponseDTO> proveedores = proveedorUseCase.findByNombreContaining(nombre);
        return ResponseEntity.ok(ApiResponse.success(proveedores));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo proveedor", description = "Crea un nuevo proveedor en el sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> createProveedor(
            @Valid @RequestBody ProveedorRequestDTO request) {
        ProveedorResponseDTO proveedor = proveedorUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(proveedor));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor", description = "Actualiza un proveedor existente")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> updateProveedor(
            @Parameter(description = "ID del proveedor") @PathVariable Long id,
            @Valid @RequestBody ProveedorUpdateDTO request) {
        ProveedorResponseDTO proveedor = proveedorUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(proveedor));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proveedor", description = "Elimina un proveedor del sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Void>> deleteProveedor(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        proveedorUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/exists/{id}")
    @Operation(summary = "Verificar si existe proveedor", description = "Verifica si existe un proveedor con el ID especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsById(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        boolean exists = proveedorUseCase.existsById(id);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists-nombre")
    @Operation(summary = "Verificar si existe nombre", description = "Verifica si existe un proveedor con el nombre especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsByNombre(
            @Parameter(description = "Nombre del proveedor") @RequestParam String nombre) {
        boolean exists = proveedorUseCase.existsByNombre(nombre);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/{id}/productos-count")
    @Operation(summary = "Contar productos por proveedor", description = "Retorna la cantidad de productos asociados a un proveedor")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Long>> countProductosByProveedor(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        long count = proveedorUseCase.countProductosByProveedorId(id);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}