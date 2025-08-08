package com.sigr.application.mapper;

import com.sigr.application.dto.categoria.CategoriaResponseDTO;
import com.sigr.application.dto.inventario.InventarioRequestDTO;
import com.sigr.application.dto.inventario.InventarioResponseDTO;
import com.sigr.application.dto.inventario.InventarioUpdateDTO;
import com.sigr.application.dto.producto.ProductoResponseDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import com.sigr.domain.entity.Inventario;
import com.sigr.domain.entity.Producto;
import com.sigr.domain.entity.Sede;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventarioMapper {

    public Inventario toEntity(InventarioRequestDTO dto) {
        Inventario inventario = new Inventario();
        inventario.setCantidad(dto.getCantidad());
        
        if (dto.getProductoId() != null) {
            Producto producto = new Producto();
            producto.setId(dto.getProductoId());
            inventario.setProducto(producto);
        }
        
        if (dto.getSedeId() != null) {
            Sede sede = new Sede();
            sede.setId(dto.getSedeId());
            inventario.setSede(sede);
        }
        
        return inventario;
    }

    public InventarioResponseDTO toResponseDTO(Inventario inventario) {
        InventarioResponseDTO dto = new InventarioResponseDTO();
        dto.setId(inventario.getId());
        dto.setCantidad(inventario.getCantidad());
        dto.setStockBajo(inventario.getCantidad() <= 5); // Umbral por defecto
        
        if (inventario.getProducto() != null) {
            ProductoResponseDTO productoDto = new ProductoResponseDTO();
            productoDto.setId(inventario.getProducto().getId());
            productoDto.setCodigoBarra(inventario.getProducto().getCodigoBarra());
            productoDto.setNombre(inventario.getProducto().getNombre());
            productoDto.setDescripcion(inventario.getProducto().getDescripcion());
            productoDto.setPrecioCompra(inventario.getProducto().getPrecioCompra());
            productoDto.setPrecioVenta(inventario.getProducto().getPrecioVenta());
            dto.setProducto(productoDto);
            dto.setProductoId(inventario.getProducto().getId());
            
            if (inventario.getProducto().getCategoria() != null) {
                CategoriaResponseDTO categoriaDto = new CategoriaResponseDTO();
                categoriaDto.setId(inventario.getProducto().getCategoria().getId());
                categoriaDto.setNombre(inventario.getProducto().getCategoria().getNombre());
                productoDto.setCategoria(categoriaDto);
                productoDto.setCategoriaId(inventario.getProducto().getCategoria().getId());
            }
        }
        
        if (inventario.getSede() != null) {
            SedeResponseDTO sedeDto = new SedeResponseDTO();
            sedeDto.setId(inventario.getSede().getId());
            sedeDto.setNombre(inventario.getSede().getNombre());
            sedeDto.setDireccion(inventario.getSede().getDireccion());
            sedeDto.setTelefono(inventario.getSede().getTelefono());
            dto.setSede(sedeDto);
            dto.setSedeId(inventario.getSede().getId());
        }
        
        return dto;
    }

    public List<InventarioResponseDTO> toResponseDTOList(List<Inventario> inventarios) {
        return inventarios.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromUpdateDTO(InventarioUpdateDTO dto, Inventario inventario) {
        inventario.setCantidad(dto.getCantidad());
    }
}