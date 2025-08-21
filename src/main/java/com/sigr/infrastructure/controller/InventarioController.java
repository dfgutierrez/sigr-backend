package com.sigr.infrastructure.controller;

import com.sigr.application.dto.inventario.InventarioRequestDTO;
import com.sigr.application.dto.inventario.InventarioResponseDTO;
import com.sigr.application.dto.inventario.InventarioUpdateDTO;
import com.sigr.application.dto.inventario.StockAdjustmentDTO;
import com.sigr.application.port.input.InventarioUseCase;
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
@RequestMapping("/api/v1/inventarios")
@RequiredArgsConstructor
@Tag(name = "Inventarios", description = "API para gestión de inventarios")
@SecurityRequirement(name = "Bearer Authentication")
public class InventarioController {

    private final InventarioUseCase inventarioUseCase;

    @GetMapping
    @Operation(summary = "Obtener todos los inventarios", description = "Retorna una lista de todos los inventarios")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de inventarios obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> getAllInventarios() {
        List<InventarioResponseDTO> inventarios = inventarioUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(inventarios));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener inventarios paginados", description = "Retorna una página de inventarios")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<PagedResponse<InventarioResponseDTO>>> getAllInventariosPaginated(Pageable pageable) {
        Page<InventarioResponseDTO> inventariosPage = inventarioUseCase.findAllPaginated(pageable);
        PagedResponse<InventarioResponseDTO> pagedResponse = PagedResponse.of(inventariosPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inventario por ID", description = "Retorna un inventario específico por su ID")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> getInventarioById(
            @Parameter(description = "ID del inventario") @PathVariable Long id) {
        InventarioResponseDTO inventario = inventarioUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(inventario));
    }

    @GetMapping("/producto/{productoId}/sede/{sedeId}")
    @Operation(summary = "Obtener inventario por producto y sede", description = "Retorna el inventario de un producto en una sede específica")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> getInventarioByProductoAndSede(
            @Parameter(description = "ID del producto") @PathVariable Long productoId,
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId) {
        InventarioResponseDTO inventario = inventarioUseCase.findByProductoAndSede(productoId, sedeId);
        return ResponseEntity.ok(ApiResponse.success(inventario));
    }

    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "Obtener inventarios por sede", description = "Retorna todos los inventarios de una sede")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> getInventariosBySede(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId) {
        List<InventarioResponseDTO> inventarios = inventarioUseCase.findBySedeId(sedeId);
        return ResponseEntity.ok(ApiResponse.success(inventarios));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener inventarios por producto", description = "Retorna todos los inventarios de un producto en todas las sedes")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> getInventariosByProducto(
            @Parameter(description = "ID del producto") @PathVariable Long productoId) {
        List<InventarioResponseDTO> inventarios = inventarioUseCase.findByProductoId(productoId);
        return ResponseEntity.ok(ApiResponse.success(inventarios));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Obtener inventarios con stock bajo", description = "Retorna inventarios con cantidad menor o igual al parámetro")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> getLowStockInventarios(
            @Parameter(description = "Cantidad límite para considerar stock bajo") @RequestParam(defaultValue = "5") Integer cantidad) {
        List<InventarioResponseDTO> inventarios = inventarioUseCase.findLowStock(cantidad);
        return ResponseEntity.ok(ApiResponse.success(inventarios));
    }

    @GetMapping("/sede/{sedeId}/low-stock")
    @Operation(summary = "Obtener inventarios con stock bajo por sede", description = "Retorna inventarios con stock bajo de una sede específica")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> getLowStockInventariosBySede(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId,
            @Parameter(description = "Cantidad límite para considerar stock bajo") @RequestParam(defaultValue = "5") Integer cantidad) {
        List<InventarioResponseDTO> inventarios = inventarioUseCase.findLowStockBySede(sedeId, cantidad);
        return ResponseEntity.ok(ApiResponse.success(inventarios));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo inventario", description = "Crea un nuevo registro de inventario")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> createInventario(
            @Valid @RequestBody InventarioRequestDTO request) {
        InventarioResponseDTO inventario = inventarioUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(inventario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar inventario", description = "Actualiza un inventario existente")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> updateInventario(
            @Parameter(description = "ID del inventario") @PathVariable Long id,
            @Valid @RequestBody InventarioUpdateDTO request) {
        InventarioResponseDTO inventario = inventarioUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(inventario));
    }

    @PutMapping("/{id}/adjust-stock")
    @Operation(summary = "Ajustar stock", description = "Ajusta la cantidad en inventario (puede ser positivo o negativo)")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> adjustStock(
            @Parameter(description = "ID del inventario") @PathVariable Long id,
            @Parameter(description = "Cantidad a ajustar (puede ser negativa)") @RequestParam Integer ajuste,
            @Parameter(description = "Motivo del ajuste") @RequestParam(required = false) String motivo) {
        InventarioResponseDTO inventario = inventarioUseCase.adjustStock(id, ajuste, motivo);
        return ResponseEntity.ok(ApiResponse.success(inventario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar inventario", description = "Elimina un registro de inventario")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Void>> deleteInventario(
            @Parameter(description = "ID del inventario") @PathVariable Long id) {
        inventarioUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/exists")
    @Operation(summary = "Verificar si existe inventario", description = "Verifica si existe un inventario para un producto en una sede")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsByProductoAndSede(
            @Parameter(description = "ID del producto") @RequestParam Long productoId,
            @Parameter(description = "ID de la sede") @RequestParam Long sedeId) {
        boolean exists = inventarioUseCase.existsByProductoAndSede(productoId, sedeId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @PutMapping("/{id}/deduct-stock")
    @Operation(
        summary = "Descontar stock del inventario", 
        description = "Descuenta una cantidad específica del stock del inventario de una sede. Valida que el inventario pertenezca a la sede especificada y que haya suficiente stock disponible."
    )
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> deductStock(
            @Parameter(description = "ID del inventario") @PathVariable Long id,
            @Valid @RequestBody StockAdjustmentDTO request) {
        InventarioResponseDTO inventario = inventarioUseCase.adjustStock(id, request);
        return ResponseEntity.ok(ApiResponse.success(inventario, 
            String.format("Stock descontado exitosamente de la sede %d", request.getSedeId())));
    }
}