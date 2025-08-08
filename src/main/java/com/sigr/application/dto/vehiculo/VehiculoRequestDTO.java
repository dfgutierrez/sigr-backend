package com.sigr.application.dto.vehiculo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar un vehículo")
public class VehiculoRequestDTO {

    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 20, message = "La placa no puede exceder 20 caracteres")
    @Schema(description = "Placa del vehículo", example = "ABC123")
    private String placa;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "^(moto|carro)$", message = "El tipo debe ser 'moto' o 'carro'")
    @Schema(description = "Tipo de vehículo", example = "carro", allowableValues = {"moto", "carro"})
    private String tipo;

    @Schema(description = "ID de la marca del vehículo", example = "1")
    private Long marcaId;

    @Size(max = 50, message = "El modelo no puede exceder 50 caracteres")
    @Schema(description = "Modelo del vehículo", example = "Corolla")
    private String modelo;

    @Size(max = 150, message = "El nombre del conductor no puede exceder 150 caracteres")
    @Schema(description = "Nombre del conductor", example = "Juan Pérez")
    private String nombreConductor;

    @Size(max = 50, message = "El documento no puede exceder 50 caracteres")
    @Schema(description = "Documento del conductor", example = "12345678")
    private String documento;

    @Schema(description = "Kilómetros del vehículo", example = "50000")
    private Integer km;

    @Size(max = 10, message = "La sigla no puede exceder 10 caracteres")
    @Schema(description = "Sigla del vehículo", example = "ABC")
    private String sigla;

    @NotNull(message = "El ID de la sede es obligatorio")
    @Schema(description = "ID de la sede", example = "1")
    private Long sedeId;
}