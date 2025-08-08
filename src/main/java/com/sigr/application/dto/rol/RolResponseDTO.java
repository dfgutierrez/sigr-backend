package com.sigr.application.dto.rol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para rol")
public class RolResponseDTO {

    @Schema(description = "ID único del rol", example = "1")
    private Long id;

    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String nombre;

    @Schema(description = "Cantidad de usuarios con este rol", example = "5")
    private Integer cantidadUsuarios;

    @Schema(description = "Cantidad de menús asignados a este rol", example = "12")
    private Integer cantidadMenus;
}