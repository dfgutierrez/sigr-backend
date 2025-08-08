package com.sigr.application.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoStockBajoDTO {
    
    private Long productoId;
    private String productoNombre;
    private String codigoBarra;
    private Integer cantidad;
    private Integer stockMinimo;
    private BigDecimal precioVenta;
}