package com.sigr.application.service;

import com.sigr.application.dto.rol.RolRequestDTO;
import com.sigr.application.dto.rol.RolResponseDTO;
import com.sigr.application.mapper.RolMapper;
import com.sigr.application.port.input.RolUseCase;
import com.sigr.application.port.output.RolRepositoryPort;
import com.sigr.domain.entity.Rol;
import com.sigr.domain.exception.BusinessException;
import com.sigr.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolServiceImpl implements RolUseCase {

    private final RolRepositoryPort rolRepositoryPort;
    private final RolMapper rolMapper;

    @Override
    public List<RolResponseDTO> findAll() {
        log.debug("Finding all roles");
        List<Rol> roles = rolRepositoryPort.findAll();
        return roles.stream()
                .map(this::buildRolResponseWithStats)
                .toList();
    }

    @Override
    public Page<RolResponseDTO> findAllPaginated(Pageable pageable) {
        log.debug("Finding all roles paginated with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Rol> rolesPage = rolRepositoryPort.findAllPaginated(pageable);
        return rolesPage.map(this::buildRolResponseWithStats);
    }

    @Override
    public RolResponseDTO findById(Long id) {
        log.debug("Finding role by id: {}", id);
        Rol rol = rolRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
        return buildRolResponseWithStats(rol);
    }

    @Override
    @Transactional
    public RolResponseDTO create(RolRequestDTO request) {
        log.debug("Creating new role with name: {}", request.getNombre());
        
        if (rolRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un rol con el nombre: " + request.getNombre());
        }

        Rol rol = rolMapper.toEntity(request);
        Rol savedRol = rolRepositoryPort.save(rol);
        
        log.info("Role created successfully with ID: {}", savedRol.getId());
        return buildRolResponseWithStats(savedRol);
    }

    @Override
    @Transactional
    public RolResponseDTO update(Long id, RolRequestDTO request) {
        log.debug("Updating role with id: {}", id);
        
        Rol existingRol = rolRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));

        if (!existingRol.getNombre().equals(request.getNombre()) && 
            rolRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un rol con el nombre: " + request.getNombre());
        }

        rolMapper.updateEntityFromDTO(request, existingRol);
        Rol updatedRol = rolRepositoryPort.save(existingRol);
        
        log.info("Role updated successfully with ID: {}", updatedRol.getId());
        return buildRolResponseWithStats(updatedRol);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting role with id: {}", id);
        
        if (!rolRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + id);
        }

        long usuariosCount = rolRepositoryPort.countUsuariosByRolId(id);
        if (usuariosCount > 0) {
            throw new BusinessException("No se puede eliminar el rol porque tiene " + usuariosCount + " usuario(s) asociado(s)");
        }

        rolRepositoryPort.deleteById(id);
        log.info("Role deleted successfully with ID: {}", id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        log.debug("Checking if role exists by name: {}", nombre);
        return rolRepositoryPort.existsByNombre(nombre);
    }

    @Override
    public List<RolResponseDTO> findByNombreContaining(String nombre) {
        log.debug("Finding roles by name containing: {}", nombre);
        List<Rol> roles = rolRepositoryPort.findByNombreContaining(nombre);
        return roles.stream()
                .map(this::buildRolResponseWithStats)
                .toList();
    }

    private RolResponseDTO buildRolResponseWithStats(Rol rol) {
        Integer cantidadUsuarios = Math.toIntExact(rolRepositoryPort.countUsuariosByRolId(rol.getId()));
        Integer cantidadMenus = Math.toIntExact(rolRepositoryPort.countMenusByRolId(rol.getId()));
        
        return rolMapper.toResponseDTO(rol, cantidadUsuarios, cantidadMenus);
    }
}