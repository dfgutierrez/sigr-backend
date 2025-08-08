package com.sigr.application.dto.ingreso;

import com.sigr.application.dto.sede.SedeResponseDTO;
import com.sigr.application.dto.usuario.UsuarioResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class IngresoProductoResponseDTO {

    private Long id;
    private LocalDateTime fecha;
    private UsuarioResponseDTO usuario;
    private Long usuarioId;
    private SedeResponseDTO sede;
    private Long sedeId;
    private List<DetalleIngresoResponseDTO> detalles;
}