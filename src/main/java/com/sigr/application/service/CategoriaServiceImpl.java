package com.sigr.application.service;

import com.sigr.application.dto.categoria.CategoriaRequestDTO;
import com.sigr.application.dto.categoria.CategoriaResponseDTO;
import com.sigr.application.dto.categoria.CategoriaUpdateDTO;
import com.sigr.application.mapper.CategoriaMapper;
import com.sigr.application.port.input.CategoriaUseCase;
import com.sigr.application.port.output.CategoriaRepositoryPort;
import com.sigr.application.port.output.ProductoRepositoryPort;
import com.sigr.domain.entity.Categoria;
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
public class CategoriaServiceImpl implements CategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<CategoriaResponseDTO> findAll() {
        log.debug("Finding all categorias");
        List<Categoria> categorias = categoriaRepositoryPort.findAll();
        return categoriaMapper.toResponseDTOList(categorias);
    }

    @Override
    public Page<CategoriaResponseDTO> findAllPaginated(Pageable pageable) {
        log.debug("Finding all categorias paginated with page: {}, size: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        Page<Categoria> categoriasPage = categoriaRepositoryPort.findAllPaginated(pageable);
        return categoriasPage.map(categoriaMapper::toResponseDTO);
    }

    @Override
    public CategoriaResponseDTO findById(Long id) {
        log.debug("Finding categoria by id: {}", id);
        Categoria categoria = categoriaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return categoriaMapper.toResponseDTO(categoria);
    }

    @Override
    public List<CategoriaResponseDTO> findByNombreContaining(String nombre) {
        log.debug("Finding categorias by nombre containing: {}", nombre);
        List<Categoria> categorias = categoriaRepositoryPort.findByNombreContaining(nombre);
        return categoriaMapper.toResponseDTOList(categorias);
    }

    @Override
    @Transactional
    public CategoriaResponseDTO create(CategoriaRequestDTO request) {
        log.debug("Creating new categoria: {}", request.getNombre());

        if (categoriaRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + request.getNombre());
        }

        Categoria categoria = categoriaMapper.toEntity(request);
        Categoria savedCategoria = categoriaRepositoryPort.save(categoria);
        log.info("Categoria created successfully with id: {}", savedCategoria.getId());
        return categoriaMapper.toResponseDTO(savedCategoria);
    }

    @Override
    @Transactional
    public CategoriaResponseDTO update(Long id, CategoriaUpdateDTO request) {
        log.debug("Updating categoria with id: {}", id);

        Categoria existingCategoria = categoriaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        if (!existingCategoria.getNombre().equals(request.getNombre()) && 
            categoriaRepositoryPort.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + request.getNombre());
        }

        categoriaMapper.updateEntityFromUpdateDTO(request, existingCategoria);
        Categoria updatedCategoria = categoriaRepositoryPort.save(existingCategoria);
        log.info("Categoria updated successfully with id: {}", updatedCategoria.getId());
        return categoriaMapper.toResponseDTO(updatedCategoria);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting categoria with id: {}", id);

        if (!categoriaRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }

        long productosCount = countProductosByCategoriaId(id);
        if (productosCount > 0) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene " + 
                    productosCount + " producto(s) asociado(s)");
        }

        categoriaRepositoryPort.deleteById(id);
        log.info("Categoria deleted successfully with id: {}", id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Checking if categoria exists by id: {}", id);
        return categoriaRepositoryPort.existsById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        log.debug("Checking if categoria exists by nombre: {}", nombre);
        return categoriaRepositoryPort.existsByNombre(nombre);
    }

    @Override
    public long countProductosByCategoriaId(Long categoriaId) {
        log.debug("Counting productos by categoria id: {}", categoriaId);
        return productoRepositoryPort.findByCategoriaId(categoriaId).size();
    }
}