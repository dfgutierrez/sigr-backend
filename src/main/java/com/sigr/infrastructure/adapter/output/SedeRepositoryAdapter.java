package com.sigr.infrastructure.adapter.output;

import com.sigr.application.port.output.SedeRepositoryPort;
import com.sigr.domain.entity.Sede;
import com.sigr.domain.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SedeRepositoryAdapter implements SedeRepositoryPort {

    private final SedeRepository sedeRepository;

    @Override
    public List<Sede> findAll() {
        return sedeRepository.findAll();
    }

    @Override
    public Page<Sede> findAllPaginated(Pageable pageable) {
        return sedeRepository.findAll(pageable);
    }

    @Override
    public Optional<Sede> findById(Long id) {
        return sedeRepository.findById(id);
    }

    @Override
    public Sede save(Sede sede) {
        return sedeRepository.save(sede);
    }

    @Override
    public void deleteById(Long id) {
        sedeRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return sedeRepository.existsById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return sedeRepository.existsByNombre(nombre);
    }

    @Override
    public List<Sede> findByNombreContaining(String nombre) {
        return sedeRepository.findByNombreContaining(nombre);
    }

    @Override
    public long countVehiculosBySedeId(Long sedeId) {
        return sedeRepository.countVehiculosBySedeId(sedeId);
    }

    @Override
    public long countInventarioBySedeId(Long sedeId) {
        return sedeRepository.countInventarioBySedeId(sedeId);
    }
}