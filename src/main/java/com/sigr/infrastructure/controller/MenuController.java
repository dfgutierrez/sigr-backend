package com.sigr.infrastructure.controller;

import com.sigr.application.dto.menu.MenuRequestDTO;
import com.sigr.application.dto.menu.MenuResponseDTO;
import com.sigr.application.port.input.MenuUseCase;
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
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Tag(name = "Menús", description = "API para gestión de menús del sistema")
@SecurityRequirement(name = "Bearer Authentication")
public class MenuController {

    private final MenuUseCase menuUseCase;

    @GetMapping
    @Operation(summary = "Obtener todos los menús", description = "Retorna una lista de todos los menús registrados")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de menús obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<MenuResponseDTO>>> getAllMenus() {
        List<MenuResponseDTO> menus = menuUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener menús paginados", description = "Retorna una página de menús")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<PagedResponse<MenuResponseDTO>>> getAllMenusPaginated(Pageable pageable) {
        Page<MenuResponseDTO> menusPage = menuUseCase.findAllPaginated(pageable);
        PagedResponse<MenuResponseDTO> pagedResponse = PagedResponse.of(menusPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/my-menus")
    @Operation(summary = "Obtener menús del usuario actual", 
               description = "Retorna los menús disponibles para el usuario autenticado según sus roles")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Menús obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<ApiResponse<List<MenuResponseDTO>>> getMyMenus() {
        List<MenuResponseDTO> menus = menuUseCase.findMenusForCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo menú", description = "Crea un nuevo menú en el sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<MenuResponseDTO>> createMenu(
            @Valid @RequestBody MenuRequestDTO request) {
        MenuResponseDTO menu = menuUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(menu));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar menú", description = "Actualiza un menú existente")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<MenuResponseDTO>> updateMenu(
            @Parameter(description = "ID del menú") @PathVariable Long id,
            @Valid @RequestBody MenuRequestDTO request) {
        MenuResponseDTO menu = menuUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(menu));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar menú", description = "Elimina un menú del sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(
            @Parameter(description = "ID del menú") @PathVariable Long id) {
        menuUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Obtener menús por categoría", description = "Retorna menús filtrados por categoría")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR', 'MECANICO')")
    public ResponseEntity<ApiResponse<List<MenuResponseDTO>>> getMenusByCategoria(
            @Parameter(description = "Categoría del menú") @PathVariable String categoria) {
        List<MenuResponseDTO> menus = menuUseCase.findByCategoria(categoria);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    @GetMapping("/categorias")
    @Operation(summary = "Obtener todas las categorías", description = "Retorna una lista de todas las categorías de menús")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR', 'MECANICO')")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategorias() {
        List<String> categorias = menuUseCase.findAllCategorias();
        return ResponseEntity.ok(ApiResponse.success(categorias));
    }

    @GetMapping("/exists")
    @Operation(summary = "Verificar si existe un menú", description = "Verifica si existe un menú con el nombre especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsByNombre(
            @Parameter(description = "Nombre del menú") @RequestParam String nombre) {
        boolean exists = menuUseCase.existsByNombre(nombre);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar menús por nombre", description = "Busca menús que contengan el texto especificado en el nombre")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<MenuResponseDTO>>> searchMenusByNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre) {
        List<MenuResponseDTO> menus = menuUseCase.findByNombreContaining(nombre);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener menú por ID", description = "Retorna un menú específico por su ID")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<MenuResponseDTO>> getMenuById(
            @Parameter(description = "ID del menú") @PathVariable Long id) {
        MenuResponseDTO menu = menuUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(menu));
    }
}