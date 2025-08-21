package com.sigr.application.dto.sql;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SqlQueryRequestDTO {
    
    @NotBlank(message = "La consulta SQL es obligatoria")
    private String query;
    
    private Integer maxRows = 1000; // Límite por defecto para consultas SELECT
}