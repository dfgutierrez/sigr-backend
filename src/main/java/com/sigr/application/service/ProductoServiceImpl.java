package com.sigr.application.service;

import com.sigr.application.dto.producto.ProductoRequestDTO;
import com.sigr.application.dto.producto.ProductoResponseDTO;
import com.sigr.application.dto.producto.ProductoUpdateDTO;
import com.sigr.application.dto.producto.ProductoConStockDTO;
import com.sigr.application.mapper.ProductoMapper;
import com.sigr.application.port.input.ProductoUseCase;
import com.sigr.application.port.output.CategoriaRepositoryPort;
import com.sigr.application.port.output.ProductoRepositoryPort;
import com.sigr.application.port.output.InventarioRepositoryPort;
import com.sigr.application.port.output.SedeRepositoryPort;
import com.sigr.domain.entity.Categoria;
import com.sigr.domain.entity.Producto;
import com.sigr.domain.entity.Inventario;
import com.sigr.domain.entity.Sede;
import com.sigr.domain.exception.BusinessException;
import com.sigr.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoServiceImpl implements ProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final InventarioRepositoryPort inventarioRepositoryPort;
    private final SedeRepositoryPort sedeRepositoryPort;
    private final ProductoMapper productoMapper;

    @Override
    public List<ProductoResponseDTO> findAll() {
        log.debug("Finding all productos");
        List<Producto> productos = productoRepositoryPort.findAll();
        return productos.stream()
            .map(producto -> {
                List<Inventario> inventarios = inventarioRepositoryPort.findByProductoIdWithStock(producto.getId());
                return productoMapper.toResponseDTO(producto, inventarios);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDTO> findAll(Long sedeId) {
        if (sedeId == null) {
            return findAll();
        }
        
        log.debug("Finding productos by sede: {}", sedeId);
        List<Producto> productos = productoRepositoryPort.findBySedeId(sedeId);
        return productos.stream()
            .map(producto -> {
                List<Inventario> inventarios = inventarioRepositoryPort.findByProductoIdWithStock(producto.getId());
                return productoMapper.toResponseDTO(producto, inventarios);
            })
            .collect(Collectors.toList());
    }

    @Override
    public Page<ProductoResponseDTO> findAllPaginated(Pageable pageable) {
        log.debug("Finding all productos paginated with page: {}, size: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        Page<Producto> productosPage = productoRepositoryPort.findAllPaginated(pageable);
        return productosPage.map(producto -> {
            List<Inventario> inventarios = inventarioRepositoryPort.findByProductoIdWithStock(producto.getId());
            return productoMapper.toResponseDTO(producto, inventarios);
        });
    }

    @Override
    public ProductoResponseDTO findById(Long id) {
        log.debug("Finding producto by id: {}", id);
        Producto producto = productoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        List<Inventario> inventarios = inventarioRepositoryPort.findByProductoIdWithStock(producto.getId());
        return productoMapper.toResponseDTO(producto, inventarios);
    }

    @Override
    public ProductoResponseDTO findByCodigoBarra(String codigoBarra) {
        log.debug("Finding producto by codigo barra: {}", codigoBarra);
        Producto producto = productoRepositoryPort.findByCodigoBarra(codigoBarra)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código de barras: " + codigoBarra));
        return productoMapper.toResponseDTO(producto);
    }

    @Override
    public List<ProductoResponseDTO> findByNombreContaining(String nombre) {
        log.debug("Finding productos by nombre containing: {}", nombre);
        List<Producto> productos = productoRepositoryPort.findByNombreContaining(nombre);
        return productoMapper.toResponseDTOList(productos);
    }

    @Override
    public List<ProductoResponseDTO> findByCategoriaId(Long categoriaId) {
        log.debug("Finding productos by categoria id: {}", categoriaId);
        List<Producto> productos = productoRepositoryPort.findByCategoriaId(categoriaId);
        return productoMapper.toResponseDTOList(productos);
    }

    @Override
    @Transactional
    public ProductoResponseDTO create(ProductoRequestDTO request) {
        log.debug("Creating new producto: {} for sede: {} with initial quantity: {}", 
            request.getNombre(), request.getSedeId(), request.getCantidadInicial());

        validateProductoRequest(request);

        if (productoRepositoryPort.existsByCodigoBarra(request.getCodigoBarra())) {
            throw new BusinessException("Ya existe un producto con el código de barras: " + request.getCodigoBarra());
        }

        // Validar que la sede existe
        if (!sedeRepositoryPort.existsById(request.getSedeId())) {
            throw new ResourceNotFoundException("Sede", "id", request.getSedeId());
        }

        Producto producto = productoMapper.toEntity(request);
        
        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepositoryPort.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + request.getCategoriaId()));
            producto.setCategoria(categoria);
        }

        Producto savedProducto = productoRepositoryPort.save(producto);
        
        // Crear inventario inicial automáticamente
        createInitialInventory(savedProducto, request.getSedeId(), request.getCantidadInicial());
        
        log.info("Producto created successfully with id: {} and initial inventory in sede: {} with quantity: {}", 
            savedProducto.getId(), request.getSedeId(), request.getCantidadInicial());
        
        return productoMapper.toResponseDTO(savedProducto);
    }

    @Override
    @Transactional
    public ProductoResponseDTO update(Long id, ProductoUpdateDTO request) {
        log.debug("Updating producto with id: {}", id);

        Producto existingProducto = productoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (!existingProducto.getCodigoBarra().equals(request.getCodigoBarra()) && 
            productoRepositoryPort.existsByCodigoBarra(request.getCodigoBarra())) {
            throw new BusinessException("Ya existe un producto con el código de barras: " + request.getCodigoBarra());
        }

        validateProductoUpdateRequest(request);

        productoMapper.updateEntityFromUpdateDTO(request, existingProducto);
        
        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepositoryPort.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + request.getCategoriaId()));
            existingProducto.setCategoria(categoria);
        }

        Producto updatedProducto = productoRepositoryPort.save(existingProducto);
        log.info("Producto updated successfully with id: {}", updatedProducto.getId());
        return productoMapper.toResponseDTO(updatedProducto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting producto with id: {}", id);

        if (!productoRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }

        productoRepositoryPort.deleteById(id);
        log.info("Producto deleted successfully with id: {}", id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Checking if producto exists by id: {}", id);
        return productoRepositoryPort.existsById(id);
    }

    @Override
    public boolean existsByCodigoBarra(String codigoBarra) {
        log.debug("Checking if producto exists by codigo barra: {}", codigoBarra);
        return productoRepositoryPort.existsByCodigoBarra(codigoBarra);
    }

    private void validateProductoRequest(ProductoRequestDTO request) {
        if (request.getCategoriaId() != null && !categoriaRepositoryPort.existsById(request.getCategoriaId())) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + request.getCategoriaId());
        }
    }

    private void validateProductoUpdateRequest(ProductoUpdateDTO request) {
        if (request.getCategoriaId() != null && !categoriaRepositoryPort.existsById(request.getCategoriaId())) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + request.getCategoriaId());
        }
    }

    private void createInitialInventory(Producto producto, Long sedeId, Integer cantidadInicial) {
        log.debug("Creating initial inventory for producto: {} in sede: {} with quantity: {}", 
            producto.getId(), sedeId, cantidadInicial);
        
        // Verificar que no exista ya un inventario para este producto en esta sede
        if (inventarioRepositoryPort.existsByProductoIdAndSedeId(producto.getId(), sedeId)) {
            log.warn("Inventory already exists for producto: {} in sede: {}, skipping creation", 
                producto.getId(), sedeId);
            return;
        }
        
        // Obtener la sede
        Sede sede = sedeRepositoryPort.findById(sedeId)
            .orElseThrow(() -> new ResourceNotFoundException("Sede", "id", sedeId));
        
        // Crear el inventario
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setSede(sede);
        inventario.setCantidad(cantidadInicial);
        
        inventarioRepositoryPort.save(inventario);
        
        log.info("Initial inventory created successfully for producto: {} in sede: {} with quantity: {}", 
            producto.getId(), sedeId, cantidadInicial);
    }

    @Override
    public List<ProductoConStockDTO> findBySedeWithStock(Long sedeId) {
        log.debug("Finding productos by sede with stock: {}", sedeId);
        
        if (!sedeRepositoryPort.existsById(sedeId)) {
            throw new ResourceNotFoundException("Sede", "id", sedeId);
        }
        
        return productoRepositoryPort.findProductosBySedeWithStock(sedeId);
    }

    @Override
    public List<ProductoConStockDTO> findBySedeWithStock(Long sedeId, Boolean soloConStock) {
        log.debug("Finding productos by sede: {} with stock filter: {}", sedeId, soloConStock);
        
        if (!sedeRepositoryPort.existsById(sedeId)) {
            throw new ResourceNotFoundException("Sede", "id", sedeId);
        }
        
        return productoRepositoryPort.findProductosBySedeWithStock(sedeId, soloConStock);
    }
}