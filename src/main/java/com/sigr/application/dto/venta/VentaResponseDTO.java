package com.sigr.application.dto.venta;

import com.sigr.application.dto.sede.SedeResponseDTO;
import com.sigr.application.dto.usuario.UsuarioResponseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaResponseDTO {

    private Long id;
    private LocalDateTime fecha;
    private LocalDateTime fechaEntrega;
    private UsuarioResponseDTO usuario;
    private Long usuarioId;
    private String usuarioNombre;
    private SedeResponseDTO sede;
    private Long sedeId;
    private String sedeNombre;
    private BigDecimal total;
    private Boolean estado;
    private List<DetalleVentaResponseDTO> detalles;
}