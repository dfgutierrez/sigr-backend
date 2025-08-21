package com.sigr.infrastructure.controller;

import com.sigr.application.dto.categoria.CategoriaRequestDTO;
import com.sigr.application.dto.categoria.CategoriaResponseDTO;
import com.sigr.application.dto.categoria.CategoriaUpdateDTO;
import com.sigr.application.port.input.CategoriaUseCase;
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
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "API para gestión de categorías de productos")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoriaController {

    private final CategoriaUseCase categoriaUseCase;

    @GetMapping
    @Operation(summary = "Obtener todas las categorías", description = "Retorna una lista de todas las categorías")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<CategoriaResponseDTO>>> getAllCategorias() {
        List<CategoriaResponseDTO> categorias = categoriaUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(categorias));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener categorías paginadas", description = "Retorna una página de categorías")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<PagedResponse<CategoriaResponseDTO>>> getAllCategoriasPaginated(Pageable pageable) {
        Page<CategoriaResponseDTO> categoriasPage = categoriaUseCase.findAllPaginated(pageable);
        PagedResponse<CategoriaResponseDTO> pagedResponse = PagedResponse.of(categoriasPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Retorna una categoría específica por su ID")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> getCategoriaById(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        CategoriaResponseDTO categoria = categoriaUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(categoria));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar categorías por nombre", description = "Busca categorías que contengan el texto especificado en el nombre")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<CategoriaResponseDTO>>> searchCategoriasByNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre) {
        List<CategoriaResponseDTO> categorias = categoriaUseCase.findByNombreContaining(nombre);
        return ResponseEntity.ok(ApiResponse.success(categorias));
    }

    @PostMapping
    @Operation(summary = "Crear nueva categoría", description = "Crea una nueva categoría en el sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> createCategoria(
            @Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaResponseDTO categoria = categoriaUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(categoria));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> updateCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Long id,
            @Valid @RequestBody CategoriaUpdateDTO request) {
        CategoriaResponseDTO categoria = categoriaUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(categoria));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría del sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Void>> deleteCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        categoriaUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/exists/{id}")
    @Operation(summary = "Verificar si existe categoría", description = "Verifica si existe una categoría con el ID especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsById(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        boolean exists = categoriaUseCase.existsById(id);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists-nombre")
    @Operation(summary = "Verificar si existe nombre", description = "Verifica si existe una categoría con el nombre especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsByNombre(
            @Parameter(description = "Nombre de la categoría") @RequestParam String nombre) {
        boolean exists = categoriaUseCase.existsByNombre(nombre);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/{id}/productos-count")
    @Operation(summary = "Contar productos por categoría", description = "Retorna la cantidad de productos asociados a una categoría")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Long>> countProductosByCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        long count = categoriaUseCase.countProductosByCategoriaId(id);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}