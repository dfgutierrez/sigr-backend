package com.sigr.application.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UsuarioRequestDTO {

    @NotBlank(message = "El username es obligatorio")
    @Size(max = 100, message = "El username no puede exceder 100 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
    private String password;

    @Size(max = 150, message = "El nombre completo no puede exceder 150 caracteres")
    private String nombreCompleto;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    @Size(max = 255, message = "La URL de la foto no puede exceder 255 caracteres")
    private String fotoUrl;

    private Long sedeId;

    private List<Long> roleIds;

    private MultipartFile foto;
}