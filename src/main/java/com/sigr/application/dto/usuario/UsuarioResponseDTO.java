package com.sigr.application.dto.usuario;

import com.sigr.application.dto.rol.RolResponseDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String nombreCompleto;
    private Boolean estado;
    private String fotoUrl;
    private SedeResponseDTO sede;
    private Long sedeId;
    private List<RolResponseDTO> roles;
    private List<Long> roleIds;
}