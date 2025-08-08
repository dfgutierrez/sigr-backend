package com.sigr.application.dto.reporte;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReporteMovimientosDTO {
    
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long sedeId;
    private String sedeNombre;
    private Long totalIngresos;
    private BigDecimal valorTotalIngresos;
    private Long totalVentas;
    private BigDecimal valorTotalVentas;
    private BigDecimal margenBeneficio;
}