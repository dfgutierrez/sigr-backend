package com.sigr.application.service;

import com.sigr.application.dto.venta.VentaRequestDTO;
import com.sigr.application.dto.venta.VentaResponseDTO;
import com.sigr.application.dto.venta.VentaDescripcionDTO;
import com.sigr.application.dto.venta.DetalleVentaRequestDTO;
import com.sigr.application.dto.venta.DetalleVentaResponseDTO;
import com.sigr.application.port.in.VentaUseCase;
import com.sigr.application.port.out.VentaRepositoryPort;
import com.sigr.application.port.out.DetalleVentaRepositoryPort;
import com.sigr.application.port.output.InventarioRepositoryPort;
import com.sigr.application.port.output.ProductoRepositoryPort;
import com.sigr.application.port.output.UsuarioRepositoryPort;
import com.sigr.application.port.output.SedeRepositoryPort;
import com.sigr.application.port.output.VehiculoRepositoryPort;
import com.sigr.application.mapper.VehiculoMapper;
import com.sigr.domain.entity.*;
import com.sigr.domain.exception.ResourceNotFoundException;
import com.sigr.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaUseCase {

    private final VentaRepositoryPort ventaRepositoryPort;
    private final DetalleVentaRepositoryPort detalleVentaRepositoryPort;
    private final InventarioRepositoryPort inventarioRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    // Eliminado clienteRepositoryPort
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final SedeRepositoryPort sedeRepositoryPort;
    private final VehiculoRepositoryPort vehiculoRepositoryPort;
    private final VehiculoMapper vehiculoMapper;

    @Override
    @Transactional
    public VentaResponseDTO crearVenta(VentaRequestDTO ventaRequestDTO) {
        System.out.println("=== DEBUG: Iniciando crearVenta ===");
        // Validar sede
        Sede sede = sedeRepositoryPort.findById(ventaRequestDTO.getSedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        // Obtener usuario - puede venir del request o tomar el primero activo de la sede
        Usuario usuario;
        if (ventaRequestDTO.getUsuarioId() != null) {
            usuario = usuarioRepositoryPort.findById(ventaRequestDTO.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        } else {
            // Tomar el primer usuario activo de la sede
            List<Usuario> usuariosActivos = usuarioRepositoryPort.findActiveBySedeId(ventaRequestDTO.getSedeId());
            if (usuariosActivos.isEmpty()) {
                throw new ResourceNotFoundException("No hay usuarios activos en la sede");
            }
            usuario = usuariosActivos.get(0);
        }

        // Obtener vehículo si se proporciona
        Vehiculo vehiculo = null;
        if (ventaRequestDTO.getVehiculoId() != null) {
            vehiculo = vehiculoRepositoryPort.findById(ventaRequestDTO.getVehiculoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));
        }

        // Crear venta
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setSede(sede);
        venta.setVehiculo(vehiculo);
        venta.setFechaEntrega(ventaRequestDTO.getFechaEntrega());
        venta.setEstado(true);
        
        BigDecimal totalVenta = BigDecimal.ZERO;

        // Procesar detalles de venta
        for (DetalleVentaRequestDTO detalle : ventaRequestDTO.getDetalles()) {
            Producto producto = productoRepositoryPort.findById(detalle.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            
            // Verificar stock disponible
            Inventario inventario = inventarioRepositoryPort.findByProductoIdAndSedeId(
                    detalle.getProductoId(), ventaRequestDTO.getSedeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no disponible en esta sede"));
            
            if (inventario.getCantidad() < detalle.getCantidad()) {
                throw new BusinessException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            
            // Calcular subtotal
            BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            totalVenta = totalVenta.add(subtotal);
            
            // Actualizar inventario
            int cantidadAntes = inventario.getCantidad();
            inventario.setCantidad(inventario.getCantidad() - detalle.getCantidad());
            System.out.println("DEBUG: Producto " + detalle.getProductoId() + 
                " - Stock antes: " + cantidadAntes + 
                " - Descontando: " + detalle.getCantidad() + 
                " - Stock después: " + inventario.getCantidad());
            inventarioRepositoryPort.save(inventario);
            
            // Crear detalle de venta para el cascade
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setVenta(venta);
            detalleVenta.setProducto(producto);
            detalleVenta.setCantidad(detalle.getCantidad());
            detalleVenta.setPrecioUnitario(detalle.getPrecioUnitario());
            
            venta.getDetalles().add(detalleVenta);
        }
        
        venta.setTotal(totalVenta);
        Venta ventaGuardada = ventaRepositoryPort.save(venta);
        
        return mapToResponseDTO(ventaGuardada);
    }

    @Override
    public VentaResponseDTO obtenerVentaPorId(Long id) {
        Venta venta = ventaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        return mapToResponseDTO(venta);
    }

    @Override
    public Page<VentaResponseDTO> obtenerTodasLasVentas(Pageable pageable) {
        return ventaRepositoryPort.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<VentaResponseDTO> obtenerVentasPorSede(Long sedeId, Pageable pageable) {
        return ventaRepositoryPort.findBySedeId(sedeId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<VentaResponseDTO> obtenerVentasPorSedeYFecha(Long sedeId, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        return ventaRepositoryPort.findBySedeIdAndFechaBetween(sedeId, fechaInicio, fechaFin, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<VentaResponseDTO> obtenerVentasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        return ventaRepositoryPort.findByFechaBetween(fechaInicio, fechaFin, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<VentaResponseDTO> obtenerVentasPorUsuario(Long usuarioId, Pageable pageable) {
        return ventaRepositoryPort.findByUsuarioId(usuarioId, pageable)
                .map(this::mapToResponseDTO);
    }

    // Método eliminado - no existe concepto de cliente en este sistema

    @Override
    @Transactional
    public void anularVenta(Long id) {
        Venta venta = ventaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        
        if (!venta.getEstado()) {
            throw new BusinessException("La venta ya está anulada");
        }
        
        // Devolver stock al inventario
        List<DetalleVenta> detalles = detalleVentaRepositoryPort.findByVentaId(id);
        for (DetalleVenta detalle : detalles) {
            Inventario inventario = inventarioRepositoryPort.findByProductoIdAndSedeId(
                    detalle.getProducto().getId(), venta.getSede().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado"));
            
            inventario.setCantidad(inventario.getCantidad() + detalle.getCantidad());
            inventarioRepositoryPort.save(inventario);
        }
        
        venta.setEstado(false);
        ventaRepositoryPort.save(venta);
    }

    @Override
    public List<VentaResponseDTO> obtenerVentasDelDia(Long sedeId) {
        LocalDateTime inicioDelDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime finDelDia = inicioDelDia.plusDays(1).minusNanos(1);
        
        return ventaRepositoryPort.findBySedeIdAndFechaBetween(sedeId, inicioDelDia, finDelDia)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VentaResponseDTO> obtenerVentasPendientesPorEntregar(Pageable pageable) {
        LocalDateTime fechaActual = LocalDateTime.now();
        return ventaRepositoryPort.findByEstadoTrueAndFechaEntregaAfter(fechaActual, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<VentaResponseDTO> obtenerVentasPendientesPorEntregarPorSede(Long sedeId, Pageable pageable) {
        LocalDateTime fechaActual = LocalDateTime.now();
        return ventaRepositoryPort.findBySedeIdAndEstadoTrueAndFechaEntregaAfter(sedeId, fechaActual, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional
    public VentaResponseDTO actualizarFechaEntrega(Long id, LocalDateTime nuevaFechaEntrega) {
        Venta venta = ventaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        
        if (!venta.getEstado()) {
            throw new BusinessException("No se puede modificar la fecha de entrega de una venta anulada");
        }
        
        venta.setFechaEntrega(nuevaFechaEntrega);
        Venta ventaActualizada = ventaRepositoryPort.save(venta);
        
        return mapToResponseDTO(ventaActualizada);
    }

    @Override
    @Transactional
    public VentaResponseDTO actualizarDescripcion(Long id, VentaDescripcionDTO descripcionDTO) {
        Venta venta = ventaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        
        venta.setDescripcion(descripcionDTO.getDescripcion());
        Venta ventaActualizada = ventaRepositoryPort.save(venta);
        
        return mapToResponseDTO(ventaActualizada);
    }

    private VentaResponseDTO mapToResponseDTO(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setFechaEntrega(venta.getFechaEntrega());
        dto.setUsuarioId(venta.getUsuario().getId());
        dto.setUsuarioNombre(venta.getUsuario().getNombreCompleto());
        dto.setSedeId(venta.getSede().getId());
        dto.setSedeNombre(venta.getSede().getNombre());
        
        // Mapear información del vehículo si existe
        if (venta.getVehiculo() != null) {
            dto.setVehiculo(vehiculoMapper.toResponseDTO(venta.getVehiculo()));
            dto.setVehiculoId(venta.getVehiculo().getId());
            dto.setVehiculoPlaca(venta.getVehiculo().getPlaca());
        }
        
        dto.setTotal(venta.getTotal());
        dto.setEstado(venta.getEstado());
        dto.setDescripcion(venta.getDescripcion());
        
        // Obtener detalles
        List<DetalleVenta> detalles = detalleVentaRepositoryPort.findByVentaId(venta.getId());
        List<DetalleVentaResponseDTO> detallesDTO = detalles.stream()
                .map(this::mapDetalleToResponseDTO)
                .collect(Collectors.toList());
        dto.setDetalles(detallesDTO);
        
        return dto;
    }

    private DetalleVentaResponseDTO mapDetalleToResponseDTO(DetalleVenta detalle) {
        DetalleVentaResponseDTO dto = new DetalleVentaResponseDTO();
        dto.setId(detalle.getId());
        dto.setProductoId(detalle.getProducto().getId());
        dto.setProductoNombre(detalle.getProducto().getNombre());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        // Calcular el subtotal
        dto.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
        return dto;
    }
}