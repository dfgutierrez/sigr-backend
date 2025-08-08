package com.sigr.application.mapper;

import com.sigr.application.dto.rol.RolRequestDTO;
import com.sigr.application.dto.rol.RolResponseDTO;
import com.sigr.domain.entity.Rol;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RolMapper {

    public RolResponseDTO toResponseDTO(Rol rol) {
        if (rol == null) {
            return null;
        }

        return RolResponseDTO.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .build();
    }

    public RolResponseDTO toResponseDTO(Rol rol, Integer cantidadUsuarios, Integer cantidadMenus) {
        if (rol == null) {
            return null;
        }

        return RolResponseDTO.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .cantidadUsuarios(cantidadUsuarios)
                .cantidadMenus(cantidadMenus)
                .build();
    }

    public List<RolResponseDTO> toResponseDTOList(List<Rol> roles) {
        return roles.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Rol toEntity(RolRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Rol rol = new Rol();
        rol.setNombre(requestDTO.getNombre());
        return rol;
    }

    public void updateEntityFromDTO(RolRequestDTO requestDTO, Rol rol) {
        rol.setNombre(requestDTO.getNombre());
    }
}