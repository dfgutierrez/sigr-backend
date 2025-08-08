package com.sigr.application.dto.sede;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar una sede")
public class SedeRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre de la sede", example = "Sede Principal")
    private String nombre;

    @Schema(description = "Dirección de la sede", example = "Calle 123 #45-67, Bogotá")
    private String direccion;

    @Schema(description = "Teléfono de la sede", example = "+57 1 234-5678")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
}