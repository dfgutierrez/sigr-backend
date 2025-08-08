package com.sigr.application.mapper;

import com.sigr.application.dto.rol.RolResponseDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import com.sigr.application.dto.usuario.UsuarioRequestDTO;
import com.sigr.application.dto.usuario.UsuarioResponseDTO;
import com.sigr.application.dto.usuario.UsuarioUpdateDTO;
import com.sigr.domain.entity.Sede;
import com.sigr.domain.entity.Usuario;
import com.sigr.domain.entity.UsuarioRol;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(dto.getPassword());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setEstado(dto.getEstado());
        usuario.setFotoUrl(dto.getFotoUrl());
        
        if (dto.getSedeId() != null) {
            Sede sede = new Sede();
            sede.setId(dto.getSedeId());
            usuario.setSede(sede);
        }
        
        return usuario;
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setEstado(usuario.getEstado());
        dto.setFotoUrl(usuario.getFotoUrl());
        
        if (usuario.getSede() != null) {
            SedeResponseDTO sedeDto = new SedeResponseDTO();
            sedeDto.setId(usuario.getSede().getId());
            sedeDto.setNombre(usuario.getSede().getNombre());
            sedeDto.setDireccion(usuario.getSede().getDireccion());
            sedeDto.setTelefono(usuario.getSede().getTelefono());
            dto.setSede(sedeDto);
            dto.setSedeId(usuario.getSede().getId());
        }
        
        if (usuario.getUsuarioRoles() != null && !usuario.getUsuarioRoles().isEmpty()) {
            List<RolResponseDTO> roles = usuario.getUsuarioRoles().stream()
                    .map(usuarioRol -> {
                        RolResponseDTO rolDto = new RolResponseDTO();
                        rolDto.setId(usuarioRol.getRol().getId());
                        rolDto.setNombre(usuarioRol.getRol().getNombre());
                        return rolDto;
                    })
                    .collect(Collectors.toList());
            dto.setRoles(roles);
            
            List<Long> roleIds = usuario.getUsuarioRoles().stream()
                    .map(usuarioRol -> usuarioRol.getRol().getId())
                    .collect(Collectors.toList());
            dto.setRoleIds(roleIds);
        }
        
        return dto;
    }

    public List<UsuarioResponseDTO> toResponseDTOList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(UsuarioRequestDTO dto, Usuario usuario) {
        usuario.setUsername(dto.getUsername());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setEstado(dto.getEstado());
        usuario.setFotoUrl(dto.getFotoUrl());
        
        if (dto.getSedeId() != null) {
            Sede sede = new Sede();
            sede.setId(dto.getSedeId());
            usuario.setSede(sede);
        } else {
            usuario.setSede(null);
        }
        
        // La contraseña se actualiza solo si se proporciona
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            usuario.setPassword(dto.getPassword());
        }
    }

    public void updateEntityFromUpdateDTO(UsuarioUpdateDTO dto, Usuario usuario) {
        usuario.setUsername(dto.getUsername());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setEstado(dto.getEstado());
        usuario.setFotoUrl(dto.getFotoUrl());
        
        if (dto.getSedeId() != null) {
            Sede sede = new Sede();
            sede.setId(dto.getSedeId());
            usuario.setSede(sede);
        } else {
            usuario.setSede(null);
        }
        
        // La contraseña se actualiza solo si se proporciona
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            usuario.setPassword(dto.getPassword());
        }
    }
}