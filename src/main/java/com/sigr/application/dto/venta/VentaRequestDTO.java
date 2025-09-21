package com.sigr.application.dto.venta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaRequestDTO {

    @NotNull(message = "El ID de la sede es obligatorio")
    private Long sedeId;

    // usuarioId es opcional - si no se proporciona, se usará el primer usuario activo de la sede
    private Long usuarioId;

    // vehiculoId es opcional - puede ser null si no se asigna vehículo específico
    private Long vehiculoId;

    // fechaEntrega es opcional - puede ser null
    private LocalDateTime fechaEntrega;

    @NotEmpty(message = "Debe incluir al menos un detalle de venta")
    @Valid
    private List<DetalleVentaRequestDTO> detalles;
}