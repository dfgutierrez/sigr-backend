package com.sigr.application.service;

import com.sigr.application.dto.inventario.InventarioRequestDTO;
import com.sigr.application.dto.inventario.InventarioResponseDTO;
import com.sigr.application.dto.inventario.InventarioUpdateDTO;
import com.sigr.application.dto.inventario.StockAdjustmentDTO;
import com.sigr.application.mapper.InventarioMapper;
import com.sigr.application.port.input.InventarioUseCase;
import com.sigr.application.port.output.InventarioRepositoryPort;
import com.sigr.application.port.output.ProductoRepositoryPort;
import com.sigr.application.port.output.SedeRepositoryPort;
import com.sigr.domain.entity.Inventario;
import com.sigr.domain.entity.Producto;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventarioServiceImpl implements InventarioUseCase {

    private final InventarioRepositoryPort inventarioRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final SedeRepositoryPort sedeRepositoryPort;
    private final InventarioMapper inventarioMapper;

    @Override
    public List<InventarioResponseDTO> findAll() {
        log.debug("Finding all inventarios");
        List<Inventario> inventarios = inventarioRepositoryPort.findAll();
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Override
    public Page<InventarioResponseDTO> findAllPaginated(Pageable pageable) {
        log.debug("Finding all inventarios paginated with page: {}, size: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        Page<Inventario> inventariosPage = inventarioRepositoryPort.findAllPaginated(pageable);
        return inventariosPage.map(inventarioMapper::toResponseDTO);
    }

    @Override
    public InventarioResponseDTO findById(Long id) {
        log.debug("Finding inventario by id: {}", id);
        Inventario inventario = inventarioRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con ID: " + id));
        return inventarioMapper.toResponseDTO(inventario);
    }

    @Override
    public InventarioResponseDTO findByProductoAndSede(Long productoId, Long sedeId) {
        log.debug("Finding inventario by productoId: {} and sedeId: {}", productoId, sedeId);
        Inventario inventario = inventarioRepositoryPort.findByProductoIdAndSedeId(productoId, sedeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Inventario no encontrado para producto ID: " + productoId + " en sede ID: " + sedeId));
        return inventarioMapper.toResponseDTO(inventario);
    }

    @Override
    public List<InventarioResponseDTO> findBySedeId(Long sedeId) {
        log.debug("Finding inventarios by sedeId: {}", sedeId);
        List<Inventario> inventarios = inventarioRepositoryPort.findBySedeId(sedeId);
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Override
    public List<InventarioResponseDTO> findByProductoId(Long productoId) {
        log.debug("Finding inventarios by productoId: {}", productoId);
        List<Inventario> inventarios = inventarioRepositoryPort.findByProductoId(productoId);
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Override
    public List<InventarioResponseDTO> findLowStock(Integer cantidad) {
        log.debug("Finding inventarios with low stock: {}", cantidad);
        List<Inventario> inventarios = inventarioRepositoryPort.findByLowStock(cantidad);
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Override
    public List<InventarioResponseDTO> findLowStockBySede(Long sedeId, Integer cantidad) {
        log.debug("Finding inventarios with low stock: {} in sede: {}", cantidad, sedeId);
        List<Inventario> inventarios = inventarioRepositoryPort.findBySedeIdAndLowStock(sedeId, cantidad);
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Override
    @Transactional
    public InventarioResponseDTO create(InventarioRequestDTO request) {
        log.debug("Creating new inventario for producto: {} in sede: {}", 
                 request.getProductoId(), request.getSedeId());

        validateInventarioRequest(request);

        if (inventarioRepositoryPort.existsByProductoIdAndSedeId(request.getProductoId(), request.getSedeId())) {
            throw new BusinessException("Ya existe un inventario para este producto en esta sede");
        }

        Inventario inventario = inventarioMapper.toEntity(request);
        
        // Cargar entidades completas
        Producto producto = productoRepositoryPort.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductoId()));
        Sede sede = sedeRepositoryPort.findById(request.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con ID: " + request.getSedeId()));
        
        inventario.setProducto(producto);
        inventario.setSede(sede);

        Inventario savedInventario = inventarioRepositoryPort.save(inventario);
        log.info("Inventario created successfully with id: {}", savedInventario.getId());
        return inventarioMapper.toResponseDTO(savedInventario);
    }

    @Override
    @Transactional
    public InventarioResponseDTO update(Long id, InventarioUpdateDTO request) {
        log.debug("Updating inventario with id: {}", id);

        Inventario existingInventario = inventarioRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con ID: " + id));

        inventarioMapper.updateEntityFromUpdateDTO(request, existingInventario);

        Inventario updatedInventario = inventarioRepositoryPort.save(existingInventario);
        log.info("Inventario updated successfully with id: {}", updatedInventario.getId());
        return inventarioMapper.toResponseDTO(updatedInventario);
    }

    @Override
    @Transactional
    public InventarioResponseDTO adjustStock(Long id, Integer cantidadAjuste, String motivo) {
        log.debug("Adjusting stock for inventario id: {}, adjustment: {}, reason: {}", id, cantidadAjuste, motivo);

        Inventario inventario = inventarioRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con ID: " + id));

        int nuevaCantidad = inventario.getCantidad() + cantidadAjuste;
        if (nuevaCantidad < 0) {
            throw new BusinessException("El ajuste resultaría en cantidad negativa");
        }

        inventario.setCantidad(nuevaCantidad);
        Inventario updatedInventario = inventarioRepositoryPort.save(inventario);
        
        log.info("Stock adjusted for inventario id: {}, new quantity: {}, reason: {}", 
                id, nuevaCantidad, motivo);
        return inventarioMapper.toResponseDTO(updatedInventario);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting inventario with id: {}", id);

        if (!inventarioRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Inventario no encontrado con ID: " + id);
        }

        inventarioRepositoryPort.deleteById(id);
        log.info("Inventario deleted successfully with id: {}", id);
    }

    @Override
    public boolean existsByProductoAndSede(Long productoId, Long sedeId) {
        log.debug("Checking if inventario exists for producto: {} in sede: {}", productoId, sedeId);
        return inventarioRepositoryPort.existsByProductoIdAndSedeId(productoId, sedeId);
    }

    @Override
    @Transactional
    public InventarioResponseDTO adjustStock(Long id, StockAdjustmentDTO request) {
        log.debug("Adjusting stock for inventory id: {} by reducing: {} from sede: {}", 
            id, request.getCantidad(), request.getSedeId());
        
        Inventario inventario = inventarioRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con ID: " + id));

        // Validar que el inventario pertenece a la sede especificada
        if (!inventario.getSede().getId().equals(request.getSedeId())) {
            throw new BusinessException(
                String.format("El inventario con ID %d no pertenece a la sede %d. Pertenece a la sede %d", 
                    id, request.getSedeId(), inventario.getSede().getId())
            );
        }

        // Validar que la sede existe
        if (!sedeRepositoryPort.existsById(request.getSedeId())) {
            throw new ResourceNotFoundException("Sede no encontrada con ID: " + request.getSedeId());
        }

        // Validar que hay suficiente stock
        if (inventario.getCantidad() < request.getCantidad()) {
            throw new BusinessException(
                String.format("Stock insuficiente en sede %d. Stock actual: %d, cantidad solicitada: %d", 
                    request.getSedeId(), inventario.getCantidad(), request.getCantidad())
            );
        }

        // Descontar el stock
        int stockAnterior = inventario.getCantidad();
        int nuevoStock = stockAnterior - request.getCantidad();
        inventario.setCantidad(nuevoStock);
        
        Inventario updatedInventario = inventarioRepositoryPort.save(inventario);
        log.info("Stock adjusted successfully for inventory id: {} in sede: {}. Previous stock: {}, deducted: {}, new stock: {}. Reason: {}", 
            id, request.getSedeId(), stockAnterior, request.getCantidad(), nuevoStock, 
            request.getMotivo() != null ? request.getMotivo() : "No especificado");
        
        return inventarioMapper.toResponseDTO(updatedInventario);
    }

    private void validateInventarioRequest(InventarioRequestDTO request) {
        if (!productoRepositoryPort.existsById(request.getProductoId())) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductoId());
        }
        if (!sedeRepositoryPort.existsById(request.getSedeId())) {
            throw new ResourceNotFoundException("Sede no encontrada con ID: " + request.getSedeId());
        }
    }
}