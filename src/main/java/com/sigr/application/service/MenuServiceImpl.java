package com.sigr.application.service;

import com.sigr.application.dto.menu.MenuRequestDTO;
import com.sigr.application.dto.menu.MenuResponseDTO;
import com.sigr.application.mapper.MenuMapper;
import com.sigr.application.port.input.MenuUseCase;
import com.sigr.application.port.output.MenuRepositoryPort;
import com.sigr.application.port.output.RolRepositoryPort;
import com.sigr.domain.entity.Menu;
import com.sigr.domain.entity.MenuRol;
import com.sigr.domain.entity.Rol;
import com.sigr.domain.exception.BusinessException;
import com.sigr.domain.exception.ResourceNotFoundException;
import com.sigr.infrastructure.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuUseCase {

    private final MenuRepositoryPort menuRepositoryPort;
    private final RolRepositoryPort rolRepositoryPort;
    private final MenuMapper menuMapper;

    @Override
    public List<MenuResponseDTO> findAll() {
        log.debug("Finding all menus");
        List<Menu> menus = menuRepositoryPort.findAllWithRoles();
        return menuMapper.toResponseDTOList(menus);
    }

    @Override
    public Page<MenuResponseDTO> findAllPaginated(Pageable pageable) {
        log.debug("Finding all menus paginated with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Menu> menusPage = menuRepositoryPort.findAllPaginated(pageable);
        return menusPage.map(menuMapper::toResponseDTO);
    }

    @Override
    public List<MenuResponseDTO> findMenusForCurrentUser() {
        log.debug("Finding menus for current user");
        List<String> userRoles = getCurrentUserRoles();
        
        if (userRoles.isEmpty()) {
            log.warn("No roles found for current user");
            return List.of();
        }

        // Para todos los usuarios (incluyendo admin), filtrar por permisos de roles
        log.debug("User roles found: {}", userRoles);
        List<Menu> userMenus = menuRepositoryPort.findMenusByUserRoles(userRoles);
        log.debug("Found {} menus for user roles", userMenus.size());
        return menuMapper.toResponseDTOListWithAccess(userMenus, userRoles);
    }

    @Override
    public MenuResponseDTO findById(Long id) {
        log.debug("Finding menu by id: {}", id);
        Menu menu = menuRepositoryPort.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado con ID: " + id));
        
        List<String> userRoles = getCurrentUserRoles();
        return menuMapper.toResponseDTOWithAccess(menu, userRoles);
    }

    @Override
    @Transactional
    public MenuResponseDTO create(MenuRequestDTO request) {
        log.debug("Creating new menu: {}", request.getNombre());
        
        if (menuRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un menú con el nombre: " + request.getNombre());
        }

        validateMenuRequest(request);
        
        Menu menu = menuMapper.toEntity(request);
        Menu savedMenu = menuRepositoryPort.save(menu);
        
        // Asignar roles al menú
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            assignRolesToMenu(savedMenu, request.getRoleIds());
        }
        
        log.info("Menu created successfully with id: {}", savedMenu.getId());
        return menuMapper.toResponseDTO(savedMenu);
    }

    @Override
    @Transactional
    public MenuResponseDTO update(Long id, MenuRequestDTO request) {
        log.debug("Updating menu with id: {}", id);
        
        Menu existingMenu = menuRepositoryPort.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado con ID: " + id));

        if (!existingMenu.getNombre().equals(request.getNombre()) && 
            menuRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un menú con el nombre: " + request.getNombre());
        }

        validateMenuRequest(request);

        menuMapper.updateEntityFromDTO(request, existingMenu);
        
        // Eliminar roles existentes de la base de datos
        menuRepositoryPort.deleteMenuRolesByMenuId(existingMenu.getId());
        
        // Limpiar la colección en memoria y asignar nuevos roles
        existingMenu.getMenuRoles().clear();
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            assignRolesToMenu(existingMenu, request.getRoleIds());
        }
        
        Menu updatedMenu = menuRepositoryPort.save(existingMenu);
        log.info("Menu updated successfully with id: {}", updatedMenu.getId());
        return menuMapper.toResponseDTO(updatedMenu);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting menu with id: {}", id);
        
        if (!menuRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Menú no encontrado con ID: " + id);
        }
        
        menuRepositoryPort.deleteById(id);
        log.info("Menu deleted successfully with id: {}", id);
    }

    @Override
    public List<MenuResponseDTO> findByCategoria(String categoria) {
        log.debug("Finding menus by categoria: {}", categoria);
        List<Menu> menus = menuRepositoryPort.findByCategoriaOrderedByOrdenAndNombre(categoria);
        List<String> userRoles = getCurrentUserRoles();
        return menuMapper.toResponseDTOListWithAccess(menus, userRoles);
    }

    @Override
    public List<String> findAllCategorias() {
        log.debug("Finding all menu categorias");
        return menuRepositoryPort.findAllCategorias();
    }

    @Override
    public boolean existsByNombre(String nombre) {
        log.debug("Checking if menu exists by nombre: {}", nombre);
        return menuRepositoryPort.existsByNombre(nombre);
    }

    @Override
    public List<MenuResponseDTO> findByNombreContaining(String nombre) {
        log.debug("Finding menus by nombre containing: {}", nombre);
        List<Menu> menus = menuRepositoryPort.findByNombreContaining(nombre);
        List<String> userRoles = getCurrentUserRoles();
        return menuMapper.toResponseDTOListWithAccess(menus, userRoles);
    }

    @Override
    public boolean hasUserAccess(Long menuId, String username) {
        log.debug("Checking user access for menu {} and user {}", menuId, username);
        
        Menu menu = menuRepositoryPort.findByIdWithRoles(menuId).orElse(null);
        if (menu == null) {
            return false;
        }
        
        // Si el menú no tiene roles asignados, es accesible para todos los usuarios autenticados
        if (menu.getMenuRoles().isEmpty()) {
            return true;
        }
        
        List<String> userRoles = getCurrentUserRoles();
        List<String> menuRoles = menu.getMenuRoles().stream()
                .map(MenuRol::getRol)
                .map(Rol::getNombre)
                .toList();
        
        return userRoles.stream().anyMatch(menuRoles::contains);
    }

    private List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getRoles();
        }
        return List.of();
    }

    private void validateMenuRequest(MenuRequestDTO request) {
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            List<Rol> existingRoles = rolRepositoryPort.findByIdIn(request.getRoleIds());
            if (existingRoles.size() != request.getRoleIds().size()) {
                throw new BusinessException("Uno o más roles no existen");
            }
        }
    }

    private void assignRolesToMenu(Menu menu, List<Long> roleIds) {
        List<Rol> roles = rolRepositoryPort.findByIdIn(roleIds);
        
        for (Rol rol : roles) {
            MenuRol menuRol = new MenuRol();
            menuRol.setMenu(menu);
            menuRol.setRol(rol);
            menu.getMenuRoles().add(menuRol);
        }
    }
}