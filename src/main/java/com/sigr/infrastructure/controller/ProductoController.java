package com.sigr.infrastructure.controller;

import com.sigr.application.dto.producto.ProductoRequestDTO;
import com.sigr.application.dto.producto.ProductoResponseDTO;
import com.sigr.application.dto.producto.ProductoUpdateDTO;
import com.sigr.application.dto.producto.ProductoConStockDTO;
import com.sigr.application.port.input.ProductoUseCase;
import com.sigr.infrastructure.common.response.ApiResponse;
import com.sigr.infrastructure.common.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para gestión de productos")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductoController {

    private final ProductoUseCase productoUseCase;

    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Retorna una lista de todos los productos o de una sede específica")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> getAllProductos(
            @Parameter(description = "ID de la sede (opcional)") @RequestParam(required = false) Long sedeId) {
        List<ProductoResponseDTO> productos = productoUseCase.findAll(sedeId);
        return ResponseEntity.ok(ApiResponse.success(productos));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener productos paginados", description = "Retorna una página de productos")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<PagedResponse<ProductoResponseDTO>>> getAllProductosPaginated(Pageable pageable) {
        Page<ProductoResponseDTO> productosPage = productoUseCase.findAllPaginated(pageable);
        PagedResponse<ProductoResponseDTO> pagedResponse = PagedResponse.of(productosPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto específico por su ID")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> getProductoById(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        ProductoResponseDTO producto = productoUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @GetMapping("/codigo-barra/{codigoBarra}")
    @Operation(summary = "Obtener producto por código de barras", description = "Retorna un producto por su código de barras")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> getProductoByCodigoBarra(
            @Parameter(description = "Código de barras del producto") @PathVariable String codigoBarra) {
        ProductoResponseDTO producto = productoUseCase.findByCodigoBarra(codigoBarra);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar productos por nombre", description = "Busca productos que contengan el texto especificado en el nombre")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> searchProductosByNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre) {
        List<ProductoResponseDTO> productos = productoUseCase.findByNombreContaining(nombre);
        return ResponseEntity.ok(ApiResponse.success(productos));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Obtener productos por categoría", description = "Retorna productos de una categoría específica")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> getProductosByCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Long categoriaId) {
        List<ProductoResponseDTO> productos = productoUseCase.findByCategoriaId(categoriaId);
        return ResponseEntity.ok(ApiResponse.success(productos));
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo producto con inventario inicial", 
        description = "Crea un nuevo producto en el sistema y automáticamente crea su inventario inicial en la sede especificada"
    )
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR') or hasRole('VENDEDOR')")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> createProducto(
            @Valid @RequestBody ProductoRequestDTO request) {
        ProductoResponseDTO producto = productoUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(producto, "Producto e inventario inicial creados exitosamente"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR') or hasRole('VENDEDOR')")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> updateProducto(
            @Parameter(description = "ID del producto") @PathVariable Long id,
            @Valid @RequestBody ProductoUpdateDTO request) {
        ProductoResponseDTO producto = productoUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR') or hasRole('VENDEDOR')")
    public ResponseEntity<ApiResponse<Void>> deleteProducto(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        productoUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/exists/{id}")
    @Operation(summary = "Verificar si existe producto", description = "Verifica si existe un producto con el ID especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsById(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        boolean exists = productoUseCase.existsById(id);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists-codigo-barra")
    @Operation(summary = "Verificar si existe código de barras", description = "Verifica si existe un producto con el código de barras especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsByCodigoBarra(
            @Parameter(description = "Código de barras del producto") @RequestParam String codigoBarra) {
        boolean exists = productoUseCase.existsByCodigoBarra(codigoBarra);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "Obtener productos por sede con stock", description = "Retorna todos los productos que tienen inventario en una sede específica")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ProductoConStockDTO>>> getProductosBySede(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId) {
        List<ProductoConStockDTO> productos = productoUseCase.findBySedeWithStock(sedeId);
        return ResponseEntity.ok(ApiResponse.success(productos));
    }

    @GetMapping("/sede/{sedeId}/con-stock")
    @Operation(summary = "Obtener productos por sede filtrados por stock", description = "Retorna productos de una sede con opción de filtrar solo los que tienen stock")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VENDEDOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ProductoConStockDTO>>> getProductosBySedeWithStockFilter(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId,
            @Parameter(description = "Solo productos con stock (true) o todos (false)") @RequestParam(defaultValue = "false") Boolean soloConStock) {
        List<ProductoConStockDTO> productos = productoUseCase.findBySedeWithStock(sedeId, soloConStock);
        return ResponseEntity.ok(ApiResponse.success(productos));
    }
}