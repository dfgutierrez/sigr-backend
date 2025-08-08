package com.sigr.application.port.output;

import com.sigr.domain.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MenuRepositoryPort {
    
    List<Menu> findAll();
    
    Page<Menu> findAllPaginated(Pageable pageable);
    
    Optional<Menu> findById(Long id);
    
    Menu save(Menu menu);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    List<Menu> findAllOrderedByOrdenAndNombre();
    
    List<Menu> findByCategoriaOrderedByOrdenAndNombre(String categoria);
    
    List<String> findAllCategorias();
    
    boolean existsByNombre(String nombre);
    
    List<Menu> findByNombreContaining(String nombre);
    
    Optional<Menu> findByIdWithRoles(Long id);
    
    List<Menu> findAllWithRoles();
    
    List<Menu> findMenusByUserRoles(List<String> roles);
    
    void deleteMenuRolesByMenuId(Long menuId);
}