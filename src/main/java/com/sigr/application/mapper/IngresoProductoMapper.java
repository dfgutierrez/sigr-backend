package com.sigr.application.mapper;

import com.sigr.application.dto.categoria.CategoriaResponseDTO;
import com.sigr.application.dto.ingreso.DetalleIngresoRequestDTO;
import com.sigr.application.dto.ingreso.DetalleIngresoResponseDTO;
import com.sigr.application.dto.ingreso.IngresoProductoRequestDTO;
import com.sigr.application.dto.ingreso.IngresoProductoResponseDTO;
import com.sigr.application.dto.producto.ProductoResponseDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import com.sigr.application.dto.usuario.UsuarioResponseDTO;
import com.sigr.domain.entity.DetalleIngreso;
import com.sigr.domain.entity.IngresoProducto;
import com.sigr.domain.entity.Producto;
import com.sigr.domain.entity.Sede;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IngresoProductoMapper {

    public IngresoProducto toEntity(IngresoProductoRequestDTO dto) {
        IngresoProducto ingreso = new IngresoProducto();
        
        if (dto.getSedeId() != null) {
            Sede sede = new Sede();
            sede.setId(dto.getSedeId());
            ingreso.setSede(sede);
        }
        
        return ingreso;
    }

    public DetalleIngreso toDetalleEntity(DetalleIngresoRequestDTO dto) {
        DetalleIngreso detalle = new DetalleIngreso();
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());
        
        if (dto.getProductoId() != null) {
            Producto producto = new Producto();
            producto.setId(dto.getProductoId());
            detalle.setProducto(producto);
        }
        
        return detalle;
    }

    public IngresoProductoResponseDTO toResponseDTO(IngresoProducto ingreso) {
        IngresoProductoResponseDTO dto = new IngresoProductoResponseDTO();
        dto.setId(ingreso.getId());
        dto.setFecha(ingreso.getFecha());
        
        if (ingreso.getUsuario() != null) {
            UsuarioResponseDTO usuarioDto = new UsuarioResponseDTO();
            usuarioDto.setId(ingreso.getUsuario().getId());
            usuarioDto.setUsername(ingreso.getUsuario().getUsername());
            usuarioDto.setNombreCompleto(ingreso.getUsuario().getNombreCompleto());
            dto.setUsuario(usuarioDto);
            dto.setUsuarioId(ingreso.getUsuario().getId());
        }
        
        if (ingreso.getSede() != null) {
            SedeResponseDTO sedeDto = new SedeResponseDTO();
            sedeDto.setId(ingreso.getSede().getId());
            sedeDto.setNombre(ingreso.getSede().getNombre());
            sedeDto.setDireccion(ingreso.getSede().getDireccion());
            sedeDto.setTelefono(ingreso.getSede().getTelefono());
            dto.setSede(sedeDto);
            dto.setSedeId(ingreso.getSede().getId());
        }
        
        if (ingreso.getDetalles() != null && !ingreso.getDetalles().isEmpty()) {
            List<DetalleIngresoResponseDTO> detallesDto = ingreso.getDetalles().stream()
                    .map(this::toDetalleResponseDTO)
                    .collect(Collectors.toList());
            dto.setDetalles(detallesDto);
        }
        
        return dto;
    }

    public DetalleIngresoResponseDTO toDetalleResponseDTO(DetalleIngreso detalle) {
        DetalleIngresoResponseDTO dto = new DetalleIngresoResponseDTO();
        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
        
        if (detalle.getProducto() != null) {
            ProductoResponseDTO productoDto = new ProductoResponseDTO();
            productoDto.setId(detalle.getProducto().getId());
            productoDto.setCodigoBarra(detalle.getProducto().getCodigoBarra());
            productoDto.setNombre(detalle.getProducto().getNombre());
            productoDto.setDescripcion(detalle.getProducto().getDescripcion());
            productoDto.setPrecioCompra(detalle.getProducto().getPrecioCompra());
            productoDto.setPrecioVenta(detalle.getProducto().getPrecioVenta());
            dto.setProducto(productoDto);
            dto.setProductoId(detalle.getProducto().getId());
            
            if (detalle.getProducto().getCategoria() != null) {
                CategoriaResponseDTO categoriaDto = new CategoriaResponseDTO();
                categoriaDto.setId(detalle.getProducto().getCategoria().getId());
                categoriaDto.setNombre(detalle.getProducto().getCategoria().getNombre());
                productoDto.setCategoria(categoriaDto);
                productoDto.setCategoriaId(detalle.getProducto().getCategoria().getId());
            }
        }
        
        return dto;
    }

    public List<IngresoProductoResponseDTO> toResponseDTOList(List<IngresoProducto> ingresos) {
        return ingresos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}