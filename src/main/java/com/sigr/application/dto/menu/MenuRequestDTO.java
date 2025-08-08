package com.sigr.application.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar un menú")
public class MenuRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Schema(description = "Nombre del menú", example = "Gestión de Vehículos")
    private String nombre;

    @Schema(description = "Ruta del menú", example = "/vehiculos")
    private String ruta;

    @Schema(description = "Icono del menú", example = "fa-car")
    private String icono;

    @Schema(description = "Categoría del menú", example = "Administración")
    private String categoria;

    @Schema(description = "Orden de visualización del menú", example = "1")
    private Integer orden;

    @Schema(description = "Lista de IDs de roles que pueden acceder a este menú", example = "[1, 2, 3]")
    private List<Long> roleIds;
}