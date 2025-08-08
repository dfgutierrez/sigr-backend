package com.sigr.infrastructure.adapter.output;

import com.sigr.application.port.output.MenuRepositoryPort;
import com.sigr.domain.entity.Menu;
import com.sigr.domain.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MenuRepositoryAdapter implements MenuRepositoryPort {

    private final MenuRepository menuRepository;

    @Override
    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    @Override
    public Page<Menu> findAllPaginated(Pageable pageable) {
        return menuRepository.findAll(pageable);
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return menuRepository.findById(id);
    }

    @Override
    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    @Override
    public void deleteById(Long id) {
        menuRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return menuRepository.existsById(id);
    }

    @Override
    public List<Menu> findAllOrderedByOrdenAndNombre() {
        return menuRepository.findAllByOrderByOrdenAscNombreAsc();
    }

    @Override
    public List<Menu> findByCategoriaOrderedByOrdenAndNombre(String categoria) {
        return menuRepository.findByCategoriaOrderByOrdenAscNombreAsc(categoria);
    }

    @Override
    public List<String> findAllCategorias() {
        return menuRepository.findAllCategorias();
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return menuRepository.existsByNombre(nombre);
    }

    @Override
    public List<Menu> findByNombreContaining(String nombre) {
        return menuRepository.findByNombreContainingIgnoreCaseOrderByOrdenAscNombreAsc(nombre);
    }

    @Override
    public Optional<Menu> findByIdWithRoles(Long id) {
        return menuRepository.findByIdWithRoles(id);
    }

    @Override
    public List<Menu> findAllWithRoles() {
        return menuRepository.findAllWithRoles();
    }

    @Override
    public List<Menu> findMenusByUserRoles(List<String> roles) {
        return menuRepository.findMenusByUserRoles(roles);
    }
    
    @Override
    public void deleteMenuRolesByMenuId(Long menuId) {
        menuRepository.deleteMenuRolesByMenuId(menuId);
    }
}