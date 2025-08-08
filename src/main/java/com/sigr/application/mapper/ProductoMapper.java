package com.sigr.application.mapper;

import com.sigr.application.dto.categoria.CategoriaResponseDTO;
import com.sigr.application.dto.producto.ProductoRequestDTO;
import com.sigr.application.dto.producto.ProductoResponseDTO;
import com.sigr.application.dto.producto.ProductoUpdateDTO;
import com.sigr.domain.entity.Categoria;
import com.sigr.domain.entity.Producto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        producto.setCodigoBarra(dto.getCodigoBarra());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setPrecioVenta(dto.getPrecioVenta());
        
        if (dto.getCategoriaId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(dto.getCategoriaId());
            producto.setCategoria(categoria);
        }
        
        return producto;
    }

    public ProductoResponseDTO toResponseDTO(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setCodigoBarra(producto.getCodigoBarra());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecioCompra(producto.getPrecioCompra());
        dto.setPrecioVenta(producto.getPrecioVenta());
        
        if (producto.getCategoria() != null) {
            CategoriaResponseDTO categoriaDto = new CategoriaResponseDTO();
            categoriaDto.setId(producto.getCategoria().getId());
            categoriaDto.setNombre(producto.getCategoria().getNombre());
            dto.setCategoria(categoriaDto);
            dto.setCategoriaId(producto.getCategoria().getId());
        }
        
        return dto;
    }

    public List<ProductoResponseDTO> toResponseDTOList(List<Producto> productos) {
        return productos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromUpdateDTO(ProductoUpdateDTO dto, Producto producto) {
        producto.setCodigoBarra(dto.getCodigoBarra());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setPrecioVenta(dto.getPrecioVenta());
        
        if (dto.getCategoriaId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(dto.getCategoriaId());
            producto.setCategoria(categoria);
        } else {
            producto.setCategoria(null);
        }
    }
}