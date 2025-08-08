package com.sigr.application.dto.ingreso;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class IngresoProductoRequestDTO {

    @NotNull(message = "El ID de la sede es obligatorio")
    private Long sedeId;

    @NotEmpty(message = "Debe incluir al menos un detalle de ingreso")
    @Valid
    private List<DetalleIngresoRequestDTO> detalles;
}