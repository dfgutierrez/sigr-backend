package com.sigr.application.dto.marca;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarcaUpdateDTO {

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
}