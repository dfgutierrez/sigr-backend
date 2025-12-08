package com.sigr.application.dto.venta;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VentaDescripcionDTO {

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;
}