package com.sigr.application.service;

import com.sigr.application.dto.ingreso.DetalleIngresoRequestDTO;
import com.sigr.application.dto.ingreso.IngresoProductoRequestDTO;
import com.sigr.application.dto.ingreso.IngresoProductoResponseDTO;
import com.sigr.application.mapper.IngresoProductoMapper;
import com.sigr.application.port.input.IngresoProductoUseCase;
import com.sigr.application.port.output.IngresoProductoRepositoryPort;
import com.sigr.application.port.output.InventarioRepositoryPort;
import com.sigr.application.port.output.ProductoRepositoryPort;
import com.sigr.application.port.output.SedeRepositoryPort;
import com.sigr.application.port.output.UsuarioRepositoryPort;
import com.sigr.domain.entity.DetalleIngreso;
import com.sigr.domain.entity.IngresoProducto;
import com.sigr.domain.entity.Inventario;
import com.sigr.domain.entity.Producto;
import com.sigr.domain.entity.Sede;
import com.sigr.domain.entity.Usuario;
import com.sigr.domain.exception.BusinessException;
import com.sigr.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngresoProductoServiceImpl implements IngresoProductoUseCase {

    private final IngresoProductoRepositoryPort ingresoProductoRepositoryPort;
    private final InventarioRepositoryPort inventarioRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final SedeRepositoryPort sedeRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final IngresoProductoMapper ingresoProductoMapper;

    @Override
    public List<IngresoProductoResponseDTO> findAll() {
        log.debug("Finding all ingreso productos");
        List<IngresoProducto> ingresos = ingresoProductoRepositoryPort.findAll();
        return ingresoProductoMapper.toResponseDTOList(ingresos);
    }

    @Override
    public Page<IngresoProductoResponseDTO> findAllPaginated(Pageable pageable) {
        log.debug("Finding all ingreso productos paginated with page: {}, size: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        Page<IngresoProducto> ingresosPage = ingresoProductoRepositoryPort.findAllPaginated(pageable);
        return ingresosPage.map(ingresoProductoMapper::toResponseDTO);
    }

    @Override
    public IngresoProductoResponseDTO findById(Long id) {
        log.debug("Finding ingreso producto by id: {}", id);
        IngresoProducto ingreso = ingresoProductoRepositoryPort.findByIdWithDetalles(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingreso de producto no encontrado con ID: " + id));
        return ingresoProductoMapper.toResponseDTO(ingreso);
    }

    @Override
    public List<IngresoProductoResponseDTO> findBySedeId(Long sedeId) {
        log.debug("Finding ingreso productos by sedeId: {}", sedeId);
        List<IngresoProducto> ingresos = ingresoProductoRepositoryPort.findBySedeId(sedeId);
        return ingresoProductoMapper.toResponseDTOList(ingresos);
    }

    @Override
    public List<IngresoProductoResponseDTO> findByUsuarioId(Long usuarioId) {
        log.debug("Finding ingreso productos by usuarioId: {}", usuarioId);
        List<IngresoProducto> ingresos = ingresoProductoRepositoryPort.findByUsuarioId(usuarioId);
        return ingresoProductoMapper.toResponseDTOList(ingresos);
    }

    @Override
    public List<IngresoProductoResponseDTO> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Finding ingreso productos by fecha between: {} and {}", fechaInicio, fechaFin);
        List<IngresoProducto> ingresos = ingresoProductoRepositoryPort.findByFechaBetween(fechaInicio, fechaFin);
        return ingresoProductoMapper.toResponseDTOList(ingresos);
    }

    @Override
    public List<IngresoProductoResponseDTO> findBySedeIdAndFechaBetween(Long sedeId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Finding ingreso productos by sede: {} and fecha between: {} and {}", sedeId, fechaInicio, fechaFin);
        List<IngresoProducto> ingresos = ingresoProductoRepositoryPort.findBySedeIdAndFechaBetween(sedeId, fechaInicio, fechaFin);
        return ingresoProductoMapper.toResponseDTOList(ingresos);
    }

    @Override
    @Transactional
    public IngresoProductoResponseDTO create(IngresoProductoRequestDTO request) {
        log.debug("Creating new ingreso producto for sede: {}", request.getSedeId());

        validateIngresoProductoRequest(request);

        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepositoryPort.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + authentication.getName()));

        // Crear ingreso
        IngresoProducto ingreso = ingresoProductoMapper.toEntity(request);
        
        // Cargar entidades completas
        Sede sede = sedeRepositoryPort.findById(request.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con ID: " + request.getSedeId()));
        
        ingreso.setUsuario(usuario);
        ingreso.setSede(sede);

        // Procesar detalles
        for (DetalleIngresoRequestDTO detalleDto : request.getDetalles()) {
            DetalleIngreso detalle = ingresoProductoMapper.toDetalleEntity(detalleDto);
            
            Producto producto = productoRepositoryPort.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleDto.getProductoId()));
            
            detalle.setProducto(producto);
            detalle.setIngreso(ingreso);
            ingreso.getDetalles().add(detalle);

            // Actualizar inventario
            updateInventarioFromIngreso(producto, sede, detalleDto.getCantidad());
        }

        IngresoProducto savedIngreso = ingresoProductoRepositoryPort.save(ingreso);
        log.info("Ingreso producto created successfully with id: {}", savedIngreso.getId());
        return ingresoProductoMapper.toResponseDTO(savedIngreso);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting ingreso producto with id: {}", id);

        if (!ingresoProductoRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Ingreso de producto no encontrado con ID: " + id);
        }

        // Aquí podrías implementar lógica para revertir el inventario si es necesario
        // Por ahora solo eliminamos el registro
        ingresoProductoRepositoryPort.deleteById(id);
        log.info("Ingreso producto deleted successfully with id: {}", id);
    }

    private void validateIngresoProductoRequest(IngresoProductoRequestDTO request) {
        if (!sedeRepositoryPort.existsById(request.getSedeId())) {
            throw new ResourceNotFoundException("Sede no encontrada con ID: " + request.getSedeId());
        }

        for (DetalleIngresoRequestDTO detalle : request.getDetalles()) {
            if (!productoRepositoryPort.existsById(detalle.getProductoId())) {
                throw new ResourceNotFoundException("Producto no encontrado con ID: " + detalle.getProductoId());
            }
        }
    }

    private void updateInventarioFromIngreso(Producto producto, Sede sede, Integer cantidadIngresada) {
        Optional<Inventario> inventarioOpt = inventarioRepositoryPort.findByProductoIdAndSedeId(
                producto.getId(), sede.getId());

        if (inventarioOpt.isPresent()) {
            // Actualizar inventario existente
            Inventario inventario = inventarioOpt.get();
            inventario.setCantidad(inventario.getCantidad() + cantidadIngresada);
            inventarioRepositoryPort.save(inventario);
            log.debug("Updated existing inventario for producto: {} in sede: {}, new quantity: {}", 
                     producto.getId(), sede.getId(), inventario.getCantidad());
        } else {
            // Crear nuevo inventario
            Inventario nuevoInventario = new Inventario();
            nuevoInventario.setProducto(producto);
            nuevoInventario.setSede(sede);
            nuevoInventario.setCantidad(cantidadIngresada);
            inventarioRepositoryPort.save(nuevoInventario);
            log.debug("Created new inventario for producto: {} in sede: {}, quantity: {}", 
                     producto.getId(), sede.getId(), cantidadIngresada);
        }
    }
}