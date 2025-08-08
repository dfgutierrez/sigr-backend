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
@Schema(description = "DTO para menú en estructura de árbol")
public class MenuTreeDTO {

    @Schema(description = "ID único del menú", example = "1")
    private Long id;

    @Schema(description = "Nombre del menú", example = "Gestión de Vehículos")
    private String nombre;

    @Schema(description = "Ruta del menú", example = "/vehiculos")
    private String ruta;

    @Schema(description = "Icono del menú", example = "fa-car")
    private String icono;

    @Schema(description = "Orden de visualización", example = "1")
    private Integer orden;

    @Schema(description = "Lista de submenús ordenada jerárquicamente")
    private List<MenuTreeDTO> children;

    @Schema(description = "Indica si es un menú de primer nivel")
    private Boolean esRaiz;
}