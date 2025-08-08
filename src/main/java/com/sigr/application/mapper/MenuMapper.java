package com.sigr.application.mapper;

import com.sigr.application.dto.menu.MenuRequestDTO;
import com.sigr.application.dto.menu.MenuResponseDTO;
import com.sigr.domain.entity.Menu;
import com.sigr.domain.entity.MenuRol;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MenuMapper {

    public MenuResponseDTO toResponseDTO(Menu menu) {
        if (menu == null) {
            return null;
        }

        List<String> roles = menu.getMenuRoles().stream()
                .map(MenuRol::getRol)
                .map(rol -> rol.getNombre())
                .collect(Collectors.toList());

        List<Long> roleIds = menu.getMenuRoles().stream()
                .map(MenuRol::getRol)
                .map(rol -> rol.getId())
                .collect(Collectors.toList());

        return MenuResponseDTO.builder()
                .id(menu.getId())
                .nombre(menu.getNombre())
                .ruta(menu.getRuta())
                .icono(menu.getIcono())
                .categoria(menu.getCategoria())
                .orden(menu.getOrden())
                .roles(roles)
                .roleIds(roleIds)
                .tieneAcceso(false) // Se establece en el servicio según el usuario
                .build();
    }

    public MenuResponseDTO toResponseDTOWithAccess(Menu menu, List<String> userRoles) {
        MenuResponseDTO dto = toResponseDTO(menu);
        if (dto != null) {
            boolean hasAccess = hasUserAccessToMenu(menu, userRoles);
            dto.setTieneAcceso(hasAccess);
        }
        return dto;
    }

    public List<MenuResponseDTO> toResponseDTOList(List<Menu> menus) {
        return menus.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<MenuResponseDTO> toResponseDTOListWithAccess(List<Menu> menus, List<String> userRoles) {
        return menus.stream()
                .map(menu -> toResponseDTOWithAccess(menu, userRoles))
                .toList();
    }

    public Menu toEntity(MenuRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Menu menu = new Menu();
        menu.setNombre(requestDTO.getNombre());
        menu.setRuta(requestDTO.getRuta());
        menu.setIcono(requestDTO.getIcono());
        menu.setCategoria(requestDTO.getCategoria());
        menu.setOrden(requestDTO.getOrden());

        return menu;
    }

    public void updateEntityFromDTO(MenuRequestDTO requestDTO, Menu menu) {
        menu.setNombre(requestDTO.getNombre());
        menu.setRuta(requestDTO.getRuta());
        menu.setIcono(requestDTO.getIcono());
        menu.setCategoria(requestDTO.getCategoria());
        menu.setOrden(requestDTO.getOrden());
    }

    private boolean hasUserAccessToMenu(Menu menu, List<String> userRoles) {
        if (menu.getMenuRoles().isEmpty()) {
            return true; // Si no tiene roles asignados, es accesible para todos
        }
        
        return menu.getMenuRoles().stream()
                .map(MenuRol::getRol)
                .map(rol -> rol.getNombre())
                .anyMatch(userRoles::contains);
    }
}