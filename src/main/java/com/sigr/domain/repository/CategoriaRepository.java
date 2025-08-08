package com.sigr.domain.repository;

import com.sigr.domain.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombre(String nombre);

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}