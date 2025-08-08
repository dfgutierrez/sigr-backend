package com.sigr.application.dto.ingreso;

import com.sigr.application.dto.producto.ProductoResponseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleIngresoResponseDTO {

    private Long id;
    private ProductoResponseDTO producto;
    private Long productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}