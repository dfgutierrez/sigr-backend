package com.sigr.application.dto.producto;

import com.sigr.application.dto.categoria.CategoriaResponseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoResponseDTO {

    private Long id;
    private String codigoBarra;
    private String nombre;
    private String descripcion;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private CategoriaResponseDTO categoria;
    private Long categoriaId;
}