package com.sigr.infrastructure.adapter.output;

import com.sigr.application.port.output.VehiculoRepositoryPort;
import com.sigr.domain.entity.Vehiculo;
import com.sigr.domain.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VehiculoRepositoryAdapter implements VehiculoRepositoryPort {

    private final VehiculoRepository vehiculoRepository;

    @Override
    public List<Vehiculo> findAll() {
        return vehiculoRepository.findAll();
    }

    @Override
    public Page<Vehiculo> findAllPaginated(Pageable pageable) {
        return vehiculoRepository.findAll(pageable);
    }

    @Override
    public Optional<Vehiculo> findById(Long id) {
        return vehiculoRepository.findById(id);
    }

    @Override
    public Vehiculo save(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    @Override
    public void deleteById(Long id) {
        vehiculoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return vehiculoRepository.existsById(id);
    }

    @Override
    public boolean existsByPlaca(String placa) {
        return vehiculoRepository.existsByPlaca(placa);
    }

    @Override
    public List<Vehiculo> findByTipo(String tipo) {
        return vehiculoRepository.findByTipo(tipo);
    }

    @Override
    public List<Vehiculo> findByPlacaContaining(String placa) {
        return vehiculoRepository.findByPlacaContaining(placa);
    }

    @Override
    public List<Vehiculo> findByEstado(Boolean estado) {
        return vehiculoRepository.findByEstado(estado);
    }

    @Override
    public List<Vehiculo> findBySedeId(Long sedeId) {
        return vehiculoRepository.findBySedeId(sedeId);
    }
}