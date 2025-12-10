package com.sigr.application.dto.proveedor;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorUpdateDTO {

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    private String telefono;
}