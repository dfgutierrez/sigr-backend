package com.sigr.application.dto.producto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoSedeStockDTO {
    
    private Long sedeId;
    private String sedeNombre;
    private Integer stock;
    private Boolean tieneStock;
}