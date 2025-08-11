package com.sigr.application.port.in;

import com.sigr.application.dto.dashboard.DashboardResponseDTO;

public interface DashboardUseCase {
    
    DashboardResponseDTO obtenerDatosDashboard();
    
    DashboardResponseDTO obtenerDatosDashboardPorSede(Long sedeId);
}