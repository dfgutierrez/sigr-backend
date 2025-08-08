package com.sigr.application.mapper;

import com.sigr.application.dto.vehiculo.VehiculoRequestDTO;
import com.sigr.application.dto.vehiculo.VehiculoResponseDTO;
import com.sigr.application.dto.marca.MarcaResponseDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import com.sigr.domain.entity.Marca;
import com.sigr.domain.entity.Sede;
import com.sigr.domain.entity.Vehiculo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class VehiculoMapper {

    public VehiculoResponseDTO toResponseDTO(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return null;
        }

        return VehiculoResponseDTO.builder()
                .id(vehiculo.getId())
                .placa(vehiculo.getPlaca())
                .tipo(vehiculo.getTipo())
                .marca(buildMarcaResponseDTO(vehiculo.getMarca()))
                .marcaId(vehiculo.getMarca() != null ? vehiculo.getMarca().getId() : null)
                .modelo(vehiculo.getModelo())
                .nombreConductor(vehiculo.getNombreConductor())
                .documento(vehiculo.getDocumento())
                .km(vehiculo.getKm())
                .sigla(vehiculo.getSigla())
                .fecha(vehiculo.getFecha())
                .sede(buildSedeResponseDTO(vehiculo.getSede()))
                .sedeId(vehiculo.getSede() != null ? vehiculo.getSede().getId() : null)
                .estado(vehiculo.getEstado())
                .build();
    }

    public List<VehiculoResponseDTO> toResponseDTOList(List<Vehiculo> vehiculos) {
        return vehiculos.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Vehiculo toEntity(VehiculoRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(requestDTO.getPlaca());
        vehiculo.setTipo(requestDTO.getTipo());
        vehiculo.setModelo(requestDTO.getModelo());
        vehiculo.setNombreConductor(requestDTO.getNombreConductor());
        vehiculo.setDocumento(requestDTO.getDocumento());
        vehiculo.setKm(requestDTO.getKm());
        vehiculo.setSigla(requestDTO.getSigla());

        return vehiculo;
    }

    public void updateEntityFromDTO(VehiculoRequestDTO requestDTO, Vehiculo vehiculo) {
        vehiculo.setPlaca(requestDTO.getPlaca());
        vehiculo.setTipo(requestDTO.getTipo());
        vehiculo.setModelo(requestDTO.getModelo());
        vehiculo.setNombreConductor(requestDTO.getNombreConductor());
        vehiculo.setDocumento(requestDTO.getDocumento());
        vehiculo.setKm(requestDTO.getKm());
        vehiculo.setSigla(requestDTO.getSigla());
    }

    public void updateEntityFromUpdateDTO(com.sigr.application.dto.vehiculo.VehiculoUpdateDTO updateDTO, Vehiculo vehiculo) {
        if (updateDTO.getPlaca() != null) {
            vehiculo.setPlaca(updateDTO.getPlaca());
        }
        if (updateDTO.getTipo() != null) {
            vehiculo.setTipo(updateDTO.getTipo());
        }
        if (updateDTO.getModelo() != null) {
            vehiculo.setModelo(updateDTO.getModelo());
        }
        if (updateDTO.getNombreConductor() != null) {
            vehiculo.setNombreConductor(updateDTO.getNombreConductor());
        }
        if (updateDTO.getDocumento() != null) {
            vehiculo.setDocumento(updateDTO.getDocumento());
        }
        if (updateDTO.getKm() != null) {
            vehiculo.setKm(updateDTO.getKm());
        }
        if (updateDTO.getSigla() != null) {
            vehiculo.setSigla(updateDTO.getSigla());
        }
    }

    public void setRelations(Vehiculo vehiculo, Marca marca, Sede sede) {
        vehiculo.setMarca(marca);
        vehiculo.setSede(sede);
    }

    private MarcaResponseDTO buildMarcaResponseDTO(Marca marca) {
        if (marca == null) {
            return null;
        }
        
        MarcaResponseDTO marcaDto = new MarcaResponseDTO();
        marcaDto.setId(marca.getId());
        marcaDto.setNombre(marca.getNombre());
        return marcaDto;
    }

    private SedeResponseDTO buildSedeResponseDTO(Sede sede) {
        if (sede == null) {
            return null;
        }
        
        return SedeResponseDTO.builder()
                .id(sede.getId())
                .nombre(sede.getNombre())
                .direccion(sede.getDireccion())
                .build();
    }
}
