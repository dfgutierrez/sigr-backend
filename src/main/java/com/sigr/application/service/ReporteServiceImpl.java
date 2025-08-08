package com.sigr.application.service;

import com.sigr.application.dto.reporte.*;
import com.sigr.application.port.in.ReporteUseCase;
import com.sigr.application.port.out.VentaRepositoryPort;
import com.sigr.application.port.out.DetalleVentaRepositoryPort;
import com.sigr.application.port.output.InventarioRepositoryPort;
import com.sigr.application.port.output.SedeRepositoryPort;
import com.sigr.application.port.output.UsuarioRepositoryPort;
import com.sigr.domain.entity.Sede;
import com.sigr.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteUseCase {

    private final VentaRepositoryPort ventaRepositoryPort;
    private final InventarioRepositoryPort inventarioRepositoryPort;
    private final DetalleVentaRepositoryPort detalleVentaRepositoryPort;
    private final SedeRepositoryPort sedeRepositoryPort;
    // Eliminado clienteRepositoryPort
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    @Override
    public ReporteVentasDTO generarReporteVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId) {
        Sede sede = sedeRepositoryPort.findById(sedeId)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        Long cantidadVentas = ventaRepositoryPort.countVentasBySedeAndFecha(sedeId, fechaInicio, fechaFin);
        BigDecimal totalVentas = ventaRepositoryPort.sumTotalVentasBySedeAndFecha(sedeId, fechaInicio, fechaFin);
        
        if (totalVentas == null) {
            totalVentas = BigDecimal.ZERO;
        }

        BigDecimal promedioVenta = cantidadVentas > 0 ? 
                totalVentas.divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;

        ProductoMasVendidoDTO productoMasVendido = obtenerProductoMasVendido(fechaInicio, fechaFin, sedeId);

        ReporteVentasDTO reporte = new ReporteVentasDTO();
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);
        reporte.setSedeId(sedeId);
        reporte.setSedeNombre(sede.getNombre());
        reporte.setCantidadVentas(cantidadVentas);
        reporte.setTotalVentas(totalVentas);
        reporte.setPromedioVenta(promedioVenta);
        reporte.setProductoMasVendido(productoMasVendido);

        return reporte;
    }

    @Override
    public ReporteInventarioDTO generarReporteInventario(Long sedeId) {
        Sede sede = sedeRepositoryPort.findById(sedeId)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        Integer totalProductos = inventarioRepositoryPort.countProductosBySede(sedeId);
        Integer productosStockBajo = inventarioRepositoryPort.countProductosConStockBajo(sedeId);
        BigDecimal valorTotalInventario = inventarioRepositoryPort.calcularValorTotalInventario(sedeId);
        List<ProductoStockBajoDTO> productosConStockBajo = obtenerProductosConStockBajo(sedeId);

        if (valorTotalInventario == null) {
            valorTotalInventario = BigDecimal.ZERO;
        }

        ReporteInventarioDTO reporte = new ReporteInventarioDTO();
        reporte.setSedeId(sedeId);
        reporte.setSedeNombre(sede.getNombre());
        reporte.setTotalProductos(totalProductos);
        reporte.setProductosStockBajo(productosStockBajo);
        reporte.setValorTotalInventario(valorTotalInventario);
        reporte.setProductosConStockBajo(productosConStockBajo);

        return reporte;
    }

    @Override
    public ReporteMovimientosDTO generarReporteMovimientos(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId) {
        Sede sede = sedeRepositoryPort.findById(sedeId)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        // Obtener datos de ventas
        Long totalVentas = ventaRepositoryPort.countVentasBySedeAndFecha(sedeId, fechaInicio, fechaFin);
        BigDecimal valorTotalVentas = ventaRepositoryPort.sumTotalVentasBySedeAndFecha(sedeId, fechaInicio, fechaFin);

        // Obtener datos de ingresos (asumiendo que hay un repositorio para ingresos)
        Long totalIngresos = 0L; // Implementar cuando esté disponible el repositorio de ingresos
        BigDecimal valorTotalIngresos = BigDecimal.ZERO;

        if (valorTotalVentas == null) {
            valorTotalVentas = BigDecimal.ZERO;
        }

        BigDecimal margenBeneficio = valorTotalVentas.subtract(valorTotalIngresos);

        ReporteMovimientosDTO reporte = new ReporteMovimientosDTO();
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);
        reporte.setSedeId(sedeId);
        reporte.setSedeNombre(sede.getNombre());
        reporte.setTotalIngresos(totalIngresos);
        reporte.setValorTotalIngresos(valorTotalIngresos);
        reporte.setTotalVentas(totalVentas);
        reporte.setValorTotalVentas(valorTotalVentas);
        reporte.setMargenBeneficio(margenBeneficio);

        return reporte;
    }

    @Override
    public List<ProductoMasVendidoDTO> obtenerProductosMasVendidos(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId, Integer limite) {
        List<ProductoMasVendidoDTO> productos = detalleVentaRepositoryPort.findProductosMasVendidos(fechaInicio, fechaFin, sedeId);
        if (limite != null && limite > 0 && productos.size() > limite) {
            return productos.subList(0, limite);
        }
        return productos;
    }

    @Override
    public List<ProductoStockBajoDTO> obtenerProductosConStockBajo(Long sedeId) {
        return inventarioRepositoryPort.findProductosConStockBajo(sedeId);
    }

    @Override
    public ReporteVentasDTO generarReporteVentasPorUsuario(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Long cantidadVentas = ventaRepositoryPort.countVentasByUsuarioAndFecha(usuarioId, fechaInicio, fechaFin);
        BigDecimal totalVentas = ventaRepositoryPort.sumTotalVentasByUsuarioAndFecha(usuarioId, fechaInicio, fechaFin);

        if (totalVentas == null) {
            totalVentas = BigDecimal.ZERO;
        }

        BigDecimal promedioVenta = cantidadVentas > 0 ? 
                totalVentas.divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;

        ReporteVentasDTO reporte = new ReporteVentasDTO();
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);
        reporte.setCantidadVentas(cantidadVentas);
        reporte.setTotalVentas(totalVentas);
        reporte.setPromedioVenta(promedioVenta);

        return reporte;
    }

    @Override
    public List<ReporteVentasDTO> generarReporteComparativoSedes(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return sedeRepositoryPort.findAll().stream()
                .map(sede -> generarReporteVentas(fechaInicio, fechaFin, sede.getId()))
                .toList();
    }

    // Métodos de reportes de clientes eliminados - no existe entidad Cliente

    @Override
    public List<ReporteUsuariosDTO> generarReporteRendimientoUsuarios(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId) {
        return usuarioRepositoryPort.findReporteRendimientoUsuarios(fechaInicio, fechaFin, sedeId);
    }

    @Override
    public ReporteInventarioDTO generarReporteInventarioCompleto() {
        List<Sede> sedes = sedeRepositoryPort.findAll();
        
        // Para simplificar, retornamos el reporte de la primera sede
        if (!sedes.isEmpty()) {
            return generarReporteInventario(sedes.get(0).getId());
        }
        
        // Si no hay sedes, retornamos un reporte vacío
        ReporteInventarioDTO reporte = new ReporteInventarioDTO();
        reporte.setTotalProductos(0);
        reporte.setProductosStockBajo(0);
        reporte.setValorTotalInventario(BigDecimal.ZERO);
        return reporte;
    }

    private ProductoMasVendidoDTO obtenerProductoMasVendido(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long sedeId) {
        List<ProductoMasVendidoDTO> productos = obtenerProductosMasVendidos(fechaInicio, fechaFin, sedeId, 1);
        return productos.isEmpty() ? null : productos.get(0);
    }
}