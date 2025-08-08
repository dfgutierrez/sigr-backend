package com.sigr.application.dto.inventario;

import com.sigr.application.dto.producto.ProductoResponseDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import lombok.Data;

@Data
public class InventarioResponseDTO {

    private Long id;
    private ProductoResponseDTO producto;
    private Long productoId;
    private SedeResponseDTO sede;
    private Long sedeId;
    private Integer cantidad;
    private Boolean stockBajo;
}