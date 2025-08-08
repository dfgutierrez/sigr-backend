package com.sigr.domain.repository;

import com.sigr.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    List<Menu> findAllByOrderByOrdenAscNombreAsc();
    
    List<Menu> findByCategoriaOrderByOrdenAscNombreAsc(String categoria);
    
    @Query("SELECT DISTINCT m.categoria FROM Menu m WHERE m.categoria IS NOT NULL ORDER BY m.categoria")
    List<String> findAllCategorias();
    
    boolean existsByNombre(String nombre);
    
    List<Menu> findByNombreContainingIgnoreCaseOrderByOrdenAscNombreAsc(String nombre);
    
    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.menuRoles mr LEFT JOIN FETCH mr.rol WHERE m.id = :id")
    Optional<Menu> findByIdWithRoles(@Param("id") Long id);
    
    @Query("SELECT DISTINCT m FROM Menu m LEFT JOIN FETCH m.menuRoles mr LEFT JOIN FETCH mr.rol ORDER BY m.orden ASC, m.nombre ASC")
    List<Menu> findAllWithRoles();
    
    @Query("SELECT DISTINCT m FROM Menu m " +
           "WHERE EXISTS (SELECT 1 FROM MenuRol mr WHERE mr.menu = m AND mr.rol.nombre IN :roles) " +
           "ORDER BY m.orden ASC, m.nombre ASC")
    List<Menu> findMenusByUserRoles(@Param("roles") List<String> roles);
    
    @Modifying
    @Query("DELETE FROM MenuRol mr WHERE mr.menu.id = :menuId")
    void deleteMenuRolesByMenuId(@Param("menuId") Long menuId);
}