package com.sigr.application.dto.vehiculo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoSearchResultDTO {
    
    private boolean encontrado;
    private VehiculoResponseDTO vehiculo;
    private String mensaje;
    private boolean puedeRegistrar;
    
    public static VehiculoSearchResultDTO encontrado(VehiculoResponseDTO vehiculo) {
        return VehiculoSearchResultDTO.builder()
                .encontrado(true)
                .vehiculo(vehiculo)
                .mensaje("Vehículo encontrado exitosamente")
                .puedeRegistrar(false)
                .build();
    }
    
    public static VehiculoSearchResultDTO noEncontrado(String placa) {
        return VehiculoSearchResultDTO.builder()
                .encontrado(false)
                .vehiculo(null)
                .mensaje("No se encontró ningún vehículo con la placa: " + placa + ". ¿Deseas registrarlo?")
                .puedeRegistrar(true)
                .build();
    }
}