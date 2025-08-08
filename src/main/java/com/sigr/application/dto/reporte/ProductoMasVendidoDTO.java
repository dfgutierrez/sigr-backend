package com.sigr.application.dto.reporte;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoMasVendidoDTO {
    
    private Long productoId;
    private String productoNombre;
    private String codigoBarra;
    private Integer cantidadVendida;
    private BigDecimal totalVentas;
}