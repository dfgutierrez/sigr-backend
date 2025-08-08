package com.sigr.application.dto.reporte;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ReporteInventarioDTO {
    
    private Long sedeId;
    private String sedeNombre;
    private Integer totalProductos;
    private Integer productosStockBajo;
    private BigDecimal valorTotalInventario;
    private List<ProductoStockBajoDTO> productosConStockBajo;
}