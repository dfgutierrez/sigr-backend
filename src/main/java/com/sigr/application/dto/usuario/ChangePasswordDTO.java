package com.sigr.application.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, max = 255, message = "La nueva contraseña debe tener entre 6 y 255 caracteres")
    private String newPassword;
}