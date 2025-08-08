package com.sigr.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(description = "DTO para registro de usuario")
public class RegisterRequestDTO {

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 100, message = "El username debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre de usuario", example = "usuario123")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña del usuario", example = "password123")
    private String password;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombreCompleto;

    @NotEmpty(message = "Debe asignar al menos un rol")
    @Schema(description = "Lista de IDs de roles", example = "[1, 2]")
    private List<Long> roleIds;

    @Schema(description = "Lista de IDs de sedes", example = "[1]")
    private List<Long> sedeIds;
}