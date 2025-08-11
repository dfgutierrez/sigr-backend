package com.sigr.application.service;

import com.sigr.application.dto.dashboard.DashboardResponseDTO;
import com.sigr.application.port.in.DashboardUseCase;
import com.sigr.application.port.out.DetalleVentaRepositoryPort;
import com.sigr.application.port.out.VentaRepositoryPort;
import com.sigr.application.port.output.VehiculoRepositoryPort;
import com.sigr.application.port.output.SedeRepositoryPort;
import com.sigr.application.port.output.InventarioRepositoryPort;
import com.sigr.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardUseCase {

    private final DetalleVentaRepositoryPort detalleVentaRepositoryPort;
    private final VentaRepositoryPort ventaRepositoryPort;
    private final VehiculoRepositoryPort vehiculoRepositoryPort;
    private final SedeRepositoryPort sedeRepositoryPort;
    private final InventarioRepositoryPort inventarioRepositoryPort;

    @Override
    public DashboardResponseDTO obtenerDatosDashboard() {
        DashboardResponseDTO response = new DashboardResponseDTO();
        
        // Calcular fechas
        LocalDateTime finDelMes = LocalDateTime.now();
        LocalDateTime inicioDelMes = finDelMes.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        LocalDateTime finDelDia = LocalDateTime.now();
        LocalDateTime inicioDelDia = finDelDia.withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        // Productos vendidos del mes por sede
        response.setProductosVendidosMesActual(
            detalleVentaRepositoryPort.contarProductosVendidosPorSedeEnPeriodo(inicioDelMes, finDelMes)
        );
        
        // Productos vendidos del día por sede
        response.setProductosVendidosDiaActual(
            detalleVentaRepositoryPort.contarProductosVendidosPorSedeEnPeriodo(inicioDelDia, finDelDia)
        );
        
        // Vehículos nuevos del día
        response.setVehiculosNuevosDiaActual(
            vehiculoRepositoryPort.countVehiculosNuevosEnPeriodo(inicioDelDia, finDelDia)
        );
        
        // Cantidad de productos por sede
        response.setProductosPorSede(
            sedeRepositoryPort.obtenerProductosPorSede()
        );
        
        return response;
    }

    @Override
    public DashboardResponseDTO obtenerDatosDashboardPorSede(Long sedeId) {
        DashboardResponseDTO response = new DashboardResponseDTO();
        
        // Obtener información de la sede
        var sede = sedeRepositoryPort.findById(sedeId)
            .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));
        
        // Calcular fechas
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime finDelMes = ahora;
        LocalDateTime inicioDelMes = finDelMes.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime finDelDia = ahora;
        LocalDateTime inicioDelDia = finDelDia.withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        // Mes anterior para KPIs
        LocalDateTime inicioMesAnterior = inicioDelMes.minusMonths(1);
        LocalDateTime finMesAnterior = inicioDelMes.minusNanos(1);
        
        // Datos básicos por sede específica
        Long productosVendidosMes = detalleVentaRepositoryPort.contarProductosVendidosEnPeriodo(sedeId, inicioDelMes, finDelMes);
        Long productosVendidosDia = detalleVentaRepositoryPort.contarProductosVendidosEnPeriodo(sedeId, inicioDelDia, finDelDia);
        
        // Productos vendidos mes actual
        DashboardResponseDTO.ProductosVendidosPorSedeDTO ventasMes = new DashboardResponseDTO.ProductosVendidosPorSedeDTO(
            sedeId, sede.getNombre(), productosVendidosMes
        );
        response.setProductosVendidosMesActual(List.of(ventasMes));
        
        // Productos vendidos día actual
        DashboardResponseDTO.ProductosVendidosPorSedeDTO ventasDia = new DashboardResponseDTO.ProductosVendidosPorSedeDTO(
            sedeId, sede.getNombre(), productosVendidosDia
        );
        response.setProductosVendidosDiaActual(List.of(ventasDia));
        
        // Vehículos nuevos del día
        response.setVehiculosNuevosDiaActual(
            vehiculoRepositoryPort.countVehiculosNuevosEnPeriodo(inicioDelDia, finDelDia)
        );
        
        // Productos por sede
        DashboardResponseDTO.ProductosPorSedeDTO productos = new DashboardResponseDTO.ProductosPorSedeDTO(
            sedeId, sede.getNombre(), sedeRepositoryPort.countInventarioBySedeId(sedeId)
        );
        response.setProductosPorSede(List.of(productos));
        
        // Ventas mensuales (últimos 6 meses)
        response.setVentasMensuales(
            ventaRepositoryPort.obtenerVentasMensualesPorSede(sedeId, 6)
        );
        
        // Productos más vendidos (top 10)
        response.setProductosMasVendidos(
            detalleVentaRepositoryPort.obtenerProductosMasVendidosPorSede(sedeId, 10)
        );
        
        // KPIs
        DashboardResponseDTO.KpisDTO kpis = new DashboardResponseDTO.KpisDTO();
        
        // Ventas mes actual
        kpis.setVentasMesActual(
            ventaRepositoryPort.obtenerKpiVentasMes(sedeId, inicioDelMes, finDelMes)
        );
        
        // Ventas mes anterior
        kpis.setVentasMesAnterior(
            ventaRepositoryPort.obtenerKpiVentasMes(sedeId, inicioMesAnterior, finMesAnterior)
        );
        
        // Inventario
        kpis.setInventario(
            inventarioRepositoryPort.obtenerKpiInventario(sedeId)
        );
        
        response.setKpis(kpis);
        
        // Fecha actual
        response.setFecha(ahora);
        
        return response;
    }
}