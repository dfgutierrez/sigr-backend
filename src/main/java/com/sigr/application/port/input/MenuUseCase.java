package com.sigr.application.port.input;

import com.sigr.application.dto.menu.MenuRequestDTO;
import com.sigr.application.dto.menu.MenuResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MenuUseCase {
    
    List<MenuResponseDTO> findAll();
    
    Page<MenuResponseDTO> findAllPaginated(Pageable pageable);
    
    List<MenuResponseDTO> findMenusForCurrentUser();
    
    MenuResponseDTO findById(Long id);
    
    MenuResponseDTO create(MenuRequestDTO request);
    
    MenuResponseDTO update(Long id, MenuRequestDTO request);
    
    void deleteById(Long id);
    
    List<MenuResponseDTO> findByCategoria(String categoria);
    
    List<String> findAllCategorias();
    
    boolean existsByNombre(String nombre);
    
    List<MenuResponseDTO> findByNombreContaining(String nombre);
    
    boolean hasUserAccess(Long menuId, String username);
}