package com.sigr.application.dto.reporte;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReporteVentasDTO {
    
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long sedeId;
    private String sedeNombre;
    private Long cantidadVentas;
    private BigDecimal totalVentas;
    private BigDecimal promedioVenta;
    private ProductoMasVendidoDTO productoMasVendido;
}