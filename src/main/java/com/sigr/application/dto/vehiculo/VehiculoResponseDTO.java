package com.sigr.application.dto.vehiculo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sigr.application.dto.marca.MarcaResponseDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para vehículo")
public class VehiculoResponseDTO {

    @Schema(description = "ID único del vehículo", example = "1")
    private Long id;

    @Schema(description = "Placa del vehículo", example = "ABC123")
    private String placa;

    @Schema(description = "Tipo de vehículo", example = "carro")
    private String tipo;

    @Schema(description = "Información de la marca")
    private MarcaResponseDTO marca;
    private Long marcaId;

    @Schema(description = "Modelo del vehículo", example = "Corolla")
    private String modelo;

    @Schema(description = "Nombre del conductor", example = "Juan Pérez")
    private String nombreConductor;

    @Schema(description = "Documento del conductor", example = "12345678")
    private String documento;

    @Schema(description = "Celular del conductor", example = "3001234567")
    private String celular;

    @Schema(description = "Kilómetros del vehículo", example = "50000")
    private Integer km;

    @Schema(description = "Sigla del vehículo", example = "ABC")
    private String sigla;

    @Schema(description = "Fecha de ingreso", example = "2024-01-15T10:30:00")
    @JsonProperty("fechaIngreso")
    private LocalDateTime fecha;

    @Schema(description = "Información de la sede")
    private SedeResponseDTO sede;
    private Long sedeId;

    @Schema(description = "Estado del vehículo", example = "true")
    private Boolean estado;
}