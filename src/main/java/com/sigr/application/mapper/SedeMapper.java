package com.sigr.application.mapper;

import com.sigr.application.dto.sede.SedeRequestDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import com.sigr.domain.entity.Sede;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SedeMapper {

    public SedeResponseDTO toResponseDTO(Sede sede) {
        if (sede == null) {
            return null;
        }

        return SedeResponseDTO.builder()
                .id(sede.getId())
                .nombre(sede.getNombre())
                .direccion(sede.getDireccion())
                .telefono(sede.getTelefono())
                .build();
    }

    public SedeResponseDTO toResponseDTO(Sede sede, Integer cantidadVehiculos, Integer cantidadProductos) {
        if (sede == null) {
            return null;
        }

        return SedeResponseDTO.builder()
                .id(sede.getId())
                .nombre(sede.getNombre())
                .direccion(sede.getDireccion())
                .telefono(sede.getTelefono())
                .cantidadVehiculos(cantidadVehiculos)
                .cantidadProductos(cantidadProductos)
                .build();
    }

    public List<SedeResponseDTO> toResponseDTOList(List<Sede> sedes) {
        return sedes.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Sede toEntity(SedeRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Sede sede = new Sede();
        sede.setNombre(requestDTO.getNombre());
        sede.setDireccion(requestDTO.getDireccion());
        sede.setTelefono(requestDTO.getTelefono());
        return sede;
    }

    public void updateEntityFromDTO(SedeRequestDTO requestDTO, Sede sede) {
        sede.setNombre(requestDTO.getNombre());
        sede.setDireccion(requestDTO.getDireccion());
        sede.setTelefono(requestDTO.getTelefono());
    }
}