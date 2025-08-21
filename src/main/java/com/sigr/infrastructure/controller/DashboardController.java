package com.sigr.infrastructure.controller;

import com.sigr.application.dto.dashboard.DashboardResponseDTO;
import com.sigr.application.port.in.DashboardUseCase;
import com.sigr.infrastructure.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "API para obtener datos del dashboard")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    @GetMapping
    @Operation(summary = "Obtener datos del dashboard global", 
               description = "Retorna datos estadísticos para el dashboard: productos vendidos del mes/día por sede, vehículos nuevos del día y productos por sede")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<DashboardResponseDTO>> getDashboardData() {
        DashboardResponseDTO dashboardData = dashboardUseCase.obtenerDatosDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboardData));
    }

    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "Obtener datos del dashboard por sede", 
               description = "Retorna datos estadísticos del dashboard filtrados por una sede específica")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<DashboardResponseDTO>> getDashboardDataBySede(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId) {
        DashboardResponseDTO dashboardData = dashboardUseCase.obtenerDatosDashboardPorSede(sedeId);
        return ResponseEntity.ok(ApiResponse.success(dashboardData));
    }
}