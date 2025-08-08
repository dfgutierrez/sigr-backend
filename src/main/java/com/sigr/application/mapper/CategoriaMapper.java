package com.sigr.application.mapper;

import com.sigr.application.dto.categoria.CategoriaRequestDTO;
import com.sigr.application.dto.categoria.CategoriaResponseDTO;
import com.sigr.application.dto.categoria.CategoriaUpdateDTO;
import com.sigr.domain.entity.Categoria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        return categoria;
    }

    public CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        return dto;
    }

    public List<CategoriaResponseDTO> toResponseDTOList(List<Categoria> categorias) {
        return categorias.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromUpdateDTO(CategoriaUpdateDTO dto, Categoria categoria) {
        categoria.setNombre(dto.getNombre());
    }
}