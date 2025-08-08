package com.sigr.application.dto.producto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoConStockDTO {
    
    // Información del producto
    private Long productoId;
    private String codigoBarra;
    private String nombre;
    private String descripcion;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    
    // Información de categoría
    private Long categoriaId;
    private String categoriaNombre;
    
    // Información de inventario en la sede
    private Long inventarioId;
    private Integer stock;
    private Long sedeId;
    private String sedeNombre;
    
    // Indicadores útiles
    private Boolean tieneStock;
    private Boolean stockBajo; // Si stock <= 5
}