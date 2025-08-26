package com.sigr.application.dto.producto;

import com.sigr.application.dto.categoria.CategoriaResponseDTO;
import com.sigr.application.dto.marca.MarcaResponseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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
    private MarcaResponseDTO marca;
    private Long marcaId;
    private List<ProductoSedeStockDTO> sedes;
}