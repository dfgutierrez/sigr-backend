package com.sigr.application.dto.inventario;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockAdjustmentDTO {
    
    @NotNull(message = "La cantidad a descontar es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;
    
    @NotNull(message = "La sede es obligatoria")
    private Long sedeId;
    
    private String motivo;
    
    private String observaciones;
}