package com.sigr.infrastructure.adapter.output;

import com.sigr.application.port.output.RolRepositoryPort;
import com.sigr.domain.entity.Rol;
import com.sigr.domain.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RolRepositoryAdapter implements RolRepositoryPort {

    private final RolRepository rolRepository;

    @Override
    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    @Override
    public Page<Rol> findAllPaginated(Pageable pageable) {
        return rolRepository.findAll(pageable);
    }

    @Override
    public Optional<Rol> findById(Long id) {
        return rolRepository.findById(id);
    }

    @Override
    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public void deleteById(Long id) {
        rolRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return rolRepository.existsById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }

    @Override
    public List<Rol> findByIdIn(List<Long> ids) {
        return rolRepository.findByIdIn(ids);
    }

    @Override
    public List<Rol> findByNombreContaining(String nombre) {
        return rolRepository.findByNombreContaining(nombre);
    }

    @Override
    public long countUsuariosByRolId(Long rolId) {
        return rolRepository.countUsuariosByRolId(rolId);
    }

    @Override
    public long countMenusByRolId(Long rolId) {
        return rolRepository.countMenusByRolId(rolId);
    }
}