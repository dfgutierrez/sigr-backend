package com.sigr.application.dto.sede;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para sede")
public class SedeResponseDTO {

    @Schema(description = "ID único de la sede", example = "1")
    private Long id;

    @Schema(description = "Nombre de la sede", example = "Sede Principal")
    private String nombre;

    @Schema(description = "Dirección de la sede", example = "Calle 123 #45-67, Bogotá")
    private String direccion;

    @Schema(description = "Teléfono de la sede", example = "+57 1 234-5678")
    private String telefono;

    @Schema(description = "Cantidad de vehículos en esta sede", example = "25")
    private Integer cantidadVehiculos;

    @Schema(description = "Cantidad de productos en esta sede", example = "150")
    private Integer cantidadProductos;
}