package com.sigr.application.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para menú")
public class MenuResponseDTO {

    @Schema(description = "ID único del menú", example = "1")
    private Long id;

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

    @Schema(description = "Lista de roles que pueden acceder a este menú")
    private List<String> roles;

    @Schema(description = "Lista de IDs de roles que pueden acceder a este menú")
    private List<Long> roleIds;

    @Schema(description = "Indica si el usuario actual tiene acceso a este menú")
    private Boolean tieneAcceso;
}