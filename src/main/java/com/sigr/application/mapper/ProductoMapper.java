package com.sigr.application.mapper;

import com.sigr.application.dto.categoria.CategoriaResponseDTO;
import com.sigr.application.dto.marca.MarcaResponseDTO;
import com.sigr.application.dto.proveedor.ProveedorResponseDTO;
import com.sigr.application.dto.producto.ProductoRequestDTO;
import com.sigr.application.dto.producto.ProductoResponseDTO;
import com.sigr.application.dto.producto.ProductoSedeStockDTO;
import com.sigr.application.dto.producto.ProductoUpdateDTO;
import com.sigr.domain.entity.Categoria;
import com.sigr.domain.entity.Inventario;
import com.sigr.domain.entity.Marca;
import com.sigr.domain.entity.Producto;
import com.sigr.domain.entity.Proveedor;
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
        
        if (dto.getMarcaId() != null) {
            Marca marca = new Marca();
            marca.setId(dto.getMarcaId());
            producto.setMarca(marca);
        }
        
        if (dto.getProveedorId() != null) {
            Proveedor proveedor = new Proveedor();
            proveedor.setId(dto.getProveedorId());
            producto.setProveedor(proveedor);
        }
        
        return producto;
    }

    public ProductoResponseDTO toResponseDTO(Producto producto) {
        return toResponseDTO(producto, null);
    }

    public ProductoResponseDTO toResponseDTO(Producto producto, List<Inventario> inventarios) {
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
        
        if (producto.getMarca() != null) {
            MarcaResponseDTO marcaDto = new MarcaResponseDTO();
            marcaDto.setId(producto.getMarca().getId());
            marcaDto.setNombre(producto.getMarca().getNombre());
            dto.setMarca(marcaDto);
            dto.setMarcaId(producto.getMarca().getId());
        }
        
        if (producto.getProveedor() != null) {
            ProveedorResponseDTO proveedorDto = new ProveedorResponseDTO();
            proveedorDto.setId(producto.getProveedor().getId());
            proveedorDto.setNombre(producto.getProveedor().getNombre());
            proveedorDto.setTelefono(producto.getProveedor().getTelefono());
            dto.setProveedor(proveedorDto);
            dto.setProveedorId(producto.getProveedor().getId());
        }
        
        if (inventarios != null) {
            List<ProductoSedeStockDTO> sedesDto = inventarios.stream()
                .map(this::toSedeStockDTO)
                .collect(Collectors.toList());
            dto.setSedes(sedesDto);
        }
        
        return dto;
    }

    private ProductoSedeStockDTO toSedeStockDTO(Inventario inventario) {
        return ProductoSedeStockDTO.builder()
            .sedeId(inventario.getSede().getId())
            .sedeNombre(inventario.getSede().getNombre())
            .stock(inventario.getCantidad())
            .tieneStock(inventario.getCantidad() > 0)
            .build();
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
        
        if (dto.getMarcaId() != null) {
            Marca marca = new Marca();
            marca.setId(dto.getMarcaId());
            producto.setMarca(marca);
        } else {
            producto.setMarca(null);
        }
        
        if (dto.getProveedorId() != null) {
            Proveedor proveedor = new Proveedor();
            proveedor.setId(dto.getProveedorId());
            producto.setProveedor(proveedor);
        } else {
            producto.setProveedor(null);
        }
    }
}