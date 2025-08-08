package com.sigr.application.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteUsuariosDTO {
    
    private Long usuarioId;
    private String usuarioNombre;
    private String sedeNombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long cantidadVentas;
    private BigDecimal totalVentas;
    private BigDecimal promedioVenta;
    private Integer ranking;
}