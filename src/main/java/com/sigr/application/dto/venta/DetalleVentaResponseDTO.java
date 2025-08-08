package com.sigr.application.dto.venta;

import com.sigr.application.dto.producto.ProductoResponseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleVentaResponseDTO {

    private Long id;
    private ProductoResponseDTO producto;
    private Long productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}