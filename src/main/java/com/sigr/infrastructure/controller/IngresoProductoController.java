package com.sigr.infrastructure.controller;

import com.sigr.application.dto.ingreso.IngresoProductoRequestDTO;
import com.sigr.application.dto.ingreso.IngresoProductoResponseDTO;
import com.sigr.application.port.input.IngresoProductoUseCase;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ingresos-productos")
@RequiredArgsConstructor
@Tag(name = "Ingresos de Productos", description = "API para gestión de ingresos de productos al inventario")
@SecurityRequirement(name = "Bearer Authentication")
public class IngresoProductoController {

    private final IngresoProductoUseCase ingresoProductoUseCase;

    @GetMapping
    @Operation(summary = "Obtener todos los ingresos de productos", description = "Retorna una lista de todos los ingresos de productos")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de ingresos obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<List<IngresoProductoResponseDTO>>> getAllIngresos() {
        List<IngresoProductoResponseDTO> ingresos = ingresoProductoUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(ingresos));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener ingresos paginados", description = "Retorna una página de ingresos de productos")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<PagedResponse<IngresoProductoResponseDTO>>> getAllIngresosPaginated(Pageable pageable) {
        Page<IngresoProductoResponseDTO> ingresosPage = ingresoProductoUseCase.findAllPaginated(pageable);
        PagedResponse<IngresoProductoResponseDTO> pagedResponse = PagedResponse.of(ingresosPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ingreso por ID", description = "Retorna un ingreso específico por su ID con todos sus detalles")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<IngresoProductoResponseDTO>> getIngresoById(
            @Parameter(description = "ID del ingreso") @PathVariable Long id) {
        IngresoProductoResponseDTO ingreso = ingresoProductoUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(ingreso));
    }

    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "Obtener ingresos por sede", description = "Retorna todos los ingresos de una sede específica")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<List<IngresoProductoResponseDTO>>> getIngresosBySede(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId) {
        List<IngresoProductoResponseDTO> ingresos = ingresoProductoUseCase.findBySedeId(sedeId);
        return ResponseEntity.ok(ApiResponse.success(ingresos));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener ingresos por usuario", description = "Retorna todos los ingresos realizados por un usuario")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<IngresoProductoResponseDTO>>> getIngresosByUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {
        List<IngresoProductoResponseDTO> ingresos = ingresoProductoUseCase.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(ingresos));
    }

    @GetMapping("/fecha-range")
    @Operation(summary = "Obtener ingresos por rango de fechas", description = "Retorna ingresos realizados entre dos fechas")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<List<IngresoProductoResponseDTO>>> getIngresosByFechaRange(
            @Parameter(description = "Fecha inicio (formato: yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha fin (formato: yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<IngresoProductoResponseDTO> ingresos = ingresoProductoUseCase.findByFechaBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(ingresos));
    }

    @GetMapping("/sede/{sedeId}/fecha-range")
    @Operation(summary = "Obtener ingresos por sede y rango de fechas", description = "Retorna ingresos de una sede en un rango de fechas")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<List<IngresoProductoResponseDTO>>> getIngresosBySedeAndFechaRange(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId,
            @Parameter(description = "Fecha inicio (formato: yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha fin (formato: yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<IngresoProductoResponseDTO> ingresos = ingresoProductoUseCase.findBySedeIdAndFechaBetween(sedeId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(ingresos));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo ingreso de productos", description = "Registra un nuevo ingreso de productos al inventario")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<IngresoProductoResponseDTO>> createIngreso(
            @Valid @RequestBody IngresoProductoRequestDTO request) {
        IngresoProductoResponseDTO ingreso = ingresoProductoUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ingreso));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ingreso", description = "Elimina un registro de ingreso (solo para administradores)")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> deleteIngreso(
            @Parameter(description = "ID del ingreso") @PathVariable Long id) {
        ingresoProductoUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}