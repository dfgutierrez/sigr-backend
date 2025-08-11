package com.sigr.infrastructure.controller;

import com.sigr.application.dto.venta.VentaRequestDTO;
import com.sigr.application.dto.venta.VentaResponseDTO;
import com.sigr.application.port.in.VentaUseCase;
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
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "API para gestión de ventas")
@SecurityRequirement(name = "Bearer Authentication")
public class VentaController {

    private final VentaUseCase ventaUseCase;

    @GetMapping
    @Operation(summary = "Obtener todas las ventas paginadas", description = "Retorna una página de ventas con parámetros de paginación")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<PagedResponse<VentaResponseDTO>>> getAllVentas(Pageable pageable) {
        Page<VentaResponseDTO> ventasPage = ventaUseCase.obtenerTodasLasVentas(pageable);
        PagedResponse<VentaResponseDTO> pagedResponse = PagedResponse.of(ventasPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PostMapping
    @Operation(summary = "Crear nueva venta", description = "Crea una nueva venta en el sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> createVenta(
            @Valid @RequestBody VentaRequestDTO request) {
        VentaResponseDTO venta = ventaUseCase.crearVenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(venta));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Retorna una venta específica por su ID")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> getVentaById(
            @Parameter(description = "ID de la venta") @PathVariable Long id) {
        VentaResponseDTO venta = ventaUseCase.obtenerVentaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(venta));
    }


    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "Obtener ventas por sede", description = "Obtiene las ventas de una sede específica con filtros opcionales de fecha")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<PagedResponse<VentaResponseDTO>>> getVentasBySede(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId,
            @Parameter(description = "Fecha desde (opcional)", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @Parameter(description = "Fecha hasta (opcional)", example = "2024-01-31T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            Pageable pageable) {
        
        Page<VentaResponseDTO> ventasPage;
        if (fechaDesde != null && fechaHasta != null) {
            ventasPage = ventaUseCase.obtenerVentasPorSedeYFecha(sedeId, fechaDesde, fechaHasta, pageable);
        } else {
            ventasPage = ventaUseCase.obtenerVentasPorSede(sedeId, pageable);
        }
        
        PagedResponse<VentaResponseDTO> pagedResponse = PagedResponse.of(ventasPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/fecha")
    @Operation(summary = "Obtener ventas por rango de fechas", description = "Obtiene las ventas en un rango de fechas")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<PagedResponse<VentaResponseDTO>>> getVentasByFecha(
            @Parameter(description = "Fecha de inicio", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha de fin", example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            Pageable pageable) {
        Page<VentaResponseDTO> ventasPage = ventaUseCase.obtenerVentasPorFecha(fechaInicio, fechaFin, pageable);
        PagedResponse<VentaResponseDTO> pagedResponse = PagedResponse.of(ventasPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener ventas por usuario", description = "Obtiene las ventas realizadas por un usuario")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<PagedResponse<VentaResponseDTO>>> getVentasByUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId, 
            Pageable pageable) {
        Page<VentaResponseDTO> ventasPage = ventaUseCase.obtenerVentasPorUsuario(usuarioId, pageable);
        PagedResponse<VentaResponseDTO> pagedResponse = PagedResponse.of(ventasPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Anular venta", description = "Anula una venta del sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> deleteVenta(
            @Parameter(description = "ID de la venta") @PathVariable Long id) {
        ventaUseCase.anularVenta(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/sede/{sedeId}/hoy")
    @Operation(summary = "Obtener ventas del día", description = "Obtiene las ventas del día actual de una sede")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<List<VentaResponseDTO>>> getVentasDelDia(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId) {
        List<VentaResponseDTO> ventas = ventaUseCase.obtenerVentasDelDia(sedeId);
        return ResponseEntity.ok(ApiResponse.success(ventas));
    }

    @GetMapping("/pendientes-por-entregar")
    @Operation(summary = "Obtener ventas pendientes por entregar", description = "Obtiene todas las ventas que están pendientes por entregar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<PagedResponse<VentaResponseDTO>>> getVentasPendientesPorEntregar(
            Pageable pageable) {
        Page<VentaResponseDTO> ventasPage = ventaUseCase.obtenerVentasPendientesPorEntregar(pageable);
        PagedResponse<VentaResponseDTO> pagedResponse = PagedResponse.of(ventasPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/sede/{sedeId}/pendientes-por-entregar")
    @Operation(summary = "Obtener ventas pendientes por entregar por sede", description = "Obtiene las ventas pendientes por entregar de una sede específica")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<PagedResponse<VentaResponseDTO>>> getVentasPendientesPorEntregarPorSede(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId,
            Pageable pageable) {
        Page<VentaResponseDTO> ventasPage = ventaUseCase.obtenerVentasPendientesPorEntregarPorSede(sedeId, pageable);
        PagedResponse<VentaResponseDTO> pagedResponse = PagedResponse.of(ventasPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PatchMapping("/{id}/fecha-entrega")
    @Operation(summary = "Actualizar fecha de entrega", description = "Actualiza la fecha de entrega de una venta")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> actualizarFechaEntrega(
            @Parameter(description = "ID de la venta") @PathVariable Long id,
            @Parameter(description = "Nueva fecha de entrega", example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEntrega) {
        VentaResponseDTO venta = ventaUseCase.actualizarFechaEntrega(id, fechaEntrega);
        return ResponseEntity.ok(ApiResponse.success(venta));
    }
}