package com.sigr.infrastructure.controller;

import com.sigr.application.dto.reporte.*;
import com.sigr.application.port.in.ReporteUseCase;
import com.sigr.infrastructure.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "API para generación de reportes")
@SecurityRequirement(name = "Bearer Authentication")
public class ReporteController {

    private final ReporteUseCase reporteUseCase;

    @GetMapping("/ventas")
    @Operation(summary = "Generar reporte de ventas", description = "Genera un reporte de ventas por sede y período")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reporte de ventas generado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ReporteVentasDTO>> generarReporteVentas(
            @Parameter(description = "Fecha de inicio", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha de fin", example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @Parameter(description = "ID de la sede")
            @RequestParam Long sedeId) {
        ReporteVentasDTO reporte = reporteUseCase.generarReporteVentas(fechaInicio, fechaFin, sedeId);
        return ResponseEntity.ok(ApiResponse.success(reporte));
    }

    @GetMapping("/inventario")
    @Operation(summary = "Generar reporte de inventario", description = "Genera un reporte del estado del inventario por sede")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<ReporteInventarioDTO>> generarReporteInventario(
            @Parameter(description = "ID de la sede")
            @RequestParam Long sedeId) {
        ReporteInventarioDTO reporte = reporteUseCase.generarReporteInventario(sedeId);
        return ResponseEntity.ok(ApiResponse.success(reporte));
    }

    @GetMapping("/movimientos")
    @Operation(summary = "Generar reporte de movimientos", description = "Genera un reporte de movimientos (ingresos y ventas) por período")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ReporteMovimientosDTO>> generarReporteMovimientos(
            @Parameter(description = "Fecha de inicio", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha de fin", example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @Parameter(description = "ID de la sede")
            @RequestParam Long sedeId) {
        ReporteMovimientosDTO reporte = reporteUseCase.generarReporteMovimientos(fechaInicio, fechaFin, sedeId);
        return ResponseEntity.ok(ApiResponse.success(reporte));
    }

    @GetMapping("/productos-mas-vendidos")
    @Operation(summary = "Obtener productos más vendidos", description = "Obtiene los productos más vendidos por período y sede")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<List<ProductoMasVendidoDTO>>> obtenerProductosMasVendidos(
            @Parameter(description = "Fecha de inicio", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha de fin", example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @Parameter(description = "ID de la sede")
            @RequestParam Long sedeId,
            @Parameter(description = "Límite de resultados", example = "10")
            @RequestParam(defaultValue = "10") Integer limite) {
        List<ProductoMasVendidoDTO> productos = reporteUseCase.obtenerProductosMasVendidos(fechaInicio, fechaFin, sedeId, limite);
        return ResponseEntity.ok(ApiResponse.success(productos));
    }

    @GetMapping("/productos-stock-bajo")
    @Operation(summary = "Obtener productos con stock bajo", description = "Obtiene los productos con stock bajo por sede")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<List<ProductoStockBajoDTO>>> obtenerProductosConStockBajo(
            @Parameter(description = "ID de la sede")
            @RequestParam Long sedeId) {
        List<ProductoStockBajoDTO> productos = reporteUseCase.obtenerProductosConStockBajo(sedeId);
        return ResponseEntity.ok(ApiResponse.success(productos));
    }

    @GetMapping("/ventas/usuario/{usuarioId}")
    @Operation(summary = "Generar reporte de ventas por usuario", description = "Genera un reporte de ventas de un usuario específico")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ReporteVentasDTO>> generarReporteVentasPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId,
            @Parameter(description = "Fecha de inicio", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha de fin", example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        ReporteVentasDTO reporte = reporteUseCase.generarReporteVentasPorUsuario(usuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(reporte));
    }

    @GetMapping("/ventas/comparativo-sedes")
    @Operation(summary = "Generar reporte comparativo de sedes", description = "Genera un reporte comparativo de ventas entre todas las sedes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<ReporteVentasDTO>>> generarReporteComparativoSedes(
            @Parameter(description = "Fecha de inicio", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha de fin", example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<ReporteVentasDTO> reportes = reporteUseCase.generarReporteComparativoSedes(fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(reportes));
    }

    @GetMapping("/usuarios/rendimiento")
    @Operation(summary = "Generar reporte de rendimiento de usuarios", description = "Genera un reporte del rendimiento de los usuarios en ventas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<ReporteUsuariosDTO>>> generarReporteRendimientoUsuarios(
            @Parameter(description = "Fecha de inicio", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha de fin", example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @Parameter(description = "ID de la sede (opcional)")
            @RequestParam(required = false) Long sedeId) {
        List<ReporteUsuariosDTO> reportes = reporteUseCase.generarReporteRendimientoUsuarios(fechaInicio, fechaFin, sedeId);
        return ResponseEntity.ok(ApiResponse.success(reportes));
    }

    @GetMapping("/inventario/completo")
    @Operation(summary = "Generar reporte completo de inventario", description = "Genera un reporte completo del inventario de todas las sedes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ReporteInventarioDTO>> generarReporteInventarioCompleto() {
        ReporteInventarioDTO reporte = reporteUseCase.generarReporteInventarioCompleto();
        return ResponseEntity.ok(ApiResponse.success(reporte));
    }
}