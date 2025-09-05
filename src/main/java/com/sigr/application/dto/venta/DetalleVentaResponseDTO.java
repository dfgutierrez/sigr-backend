package com.sigr.application.dto.venta;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleVentaResponseDTO {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}