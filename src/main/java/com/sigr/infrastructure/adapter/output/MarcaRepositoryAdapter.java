package com.sigr.infrastructure.adapter.output;

import com.sigr.application.port.output.MarcaRepositoryPort;
import com.sigr.domain.entity.Marca;
import com.sigr.domain.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MarcaRepositoryAdapter implements MarcaRepositoryPort {

    private final MarcaRepository marcaRepository;

    @Override
    public List<Marca> findAll() {
        return marcaRepository.findAll();
    }

    @Override
    public Page<Marca> findAllPaginated(Pageable pageable) {
        return marcaRepository.findAll(pageable);
    }

    @Override
    public Optional<Marca> findById(Long id) {
        return marcaRepository.findById(id);
    }

    @Override
    public List<Marca> findByNombreContaining(String nombre) {
        return marcaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Marca save(Marca marca) {
        return marcaRepository.save(marca);
    }

    @Override
    public void deleteById(Long id) {
        marcaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return marcaRepository.existsById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return marcaRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public long countVehiculosByMarcaId(Long marcaId) {
        return marcaRepository.countVehiculosByMarcaId(marcaId);
    }
}