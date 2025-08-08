package com.sigr.application.port.in;

import com.sigr.application.dto.reporte.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteUseCase {
    
    ReporteVentasDTO generarReporteVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId);
    
    ReporteInventarioDTO generarReporteInventario(Long sedeId);
    
    ReporteMovimientosDTO generarReporteMovimientos(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId);
    
    List<ProductoMasVendidoDTO> obtenerProductosMasVendidos(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId, Integer limite);
    
    List<ProductoStockBajoDTO> obtenerProductosConStockBajo(Long sedeId);
    
    ReporteVentasDTO generarReporteVentasPorUsuario(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<ReporteVentasDTO> generarReporteComparativoSedes(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Reportes de clientes eliminados - no existe entidad Cliente en el sistema
    
    List<ReporteUsuariosDTO> generarReporteRendimientoUsuarios(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId);
    
    ReporteInventarioDTO generarReporteInventarioCompleto();
}