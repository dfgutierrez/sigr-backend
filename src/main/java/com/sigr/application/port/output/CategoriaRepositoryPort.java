package com.sigr.application.port.output;

import com.sigr.domain.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepositoryPort {

    List<Categoria> findAll();

    Page<Categoria> findAllPaginated(Pageable pageable);

    Optional<Categoria> findById(Long id);

    List<Categoria> findByNombreContaining(String nombre);

    Categoria save(Categoria categoria);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByNombre(String nombre);
}