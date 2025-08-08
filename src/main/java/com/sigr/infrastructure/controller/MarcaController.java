package com.sigr.infrastructure.controller;

import com.sigr.application.dto.marca.MarcaRequestDTO;
import com.sigr.application.dto.marca.MarcaResponseDTO;
import com.sigr.application.dto.marca.MarcaUpdateDTO;
import com.sigr.application.port.input.MarcaUseCase;
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
@RequestMapping("/api/v1/marcas")
@RequiredArgsConstructor
@Tag(name = "Marcas", description = "API para gestión de marcas")
@SecurityRequirement(name = "Bearer Authentication")
public class MarcaController {

    private final MarcaUseCase marcaUseCase;

    @GetMapping
    @Operation(summary = "Obtener todas las marcas", description = "Retorna una lista de todas las marcas")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de marcas obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<List<MarcaResponseDTO>>> getAllMarcas() {
        List<MarcaResponseDTO> marcas = marcaUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(marcas));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener marcas paginadas", description = "Retorna una página de marcas")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<PagedResponse<MarcaResponseDTO>>> getAllMarcasPaginated(Pageable pageable) {
        Page<MarcaResponseDTO> marcasPage = marcaUseCase.findAllPaginated(pageable);
        PagedResponse<MarcaResponseDTO> pagedResponse = PagedResponse.of(marcasPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener marca por ID", description = "Retorna una marca específica por su ID")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<MarcaResponseDTO>> getMarcaById(
            @Parameter(description = "ID de la marca") @PathVariable Long id) {
        MarcaResponseDTO marca = marcaUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(marca));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar marcas por nombre", description = "Busca marcas que contengan el texto especificado en el nombre")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<List<MarcaResponseDTO>>> searchMarcasByNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre) {
        List<MarcaResponseDTO> marcas = marcaUseCase.findByNombreContaining(nombre);
        return ResponseEntity.ok(ApiResponse.success(marcas));
    }

    @PostMapping
    @Operation(summary = "Crear nueva marca", description = "Crea una nueva marca en el sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<MarcaResponseDTO>> createMarca(
            @Valid @RequestBody MarcaRequestDTO request) {
        MarcaResponseDTO marca = marcaUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(marca));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar marca", description = "Actualiza una marca existente")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<MarcaResponseDTO>> updateMarca(
            @Parameter(description = "ID de la marca") @PathVariable Long id,
            @Valid @RequestBody MarcaUpdateDTO request) {
        MarcaResponseDTO marca = marcaUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(marca));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar marca", description = "Elimina una marca del sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> deleteMarca(
            @Parameter(description = "ID de la marca") @PathVariable Long id) {
        marcaUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/exists/{id}")
    @Operation(summary = "Verificar si existe marca", description = "Verifica si existe una marca con el ID especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<Boolean>> existsById(
            @Parameter(description = "ID de la marca") @PathVariable Long id) {
        boolean exists = marcaUseCase.existsById(id);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists-nombre")
    @Operation(summary = "Verificar si existe nombre", description = "Verifica si existe una marca con el nombre especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<Boolean>> existsByNombre(
            @Parameter(description = "Nombre de la marca") @RequestParam String nombre) {
        boolean exists = marcaUseCase.existsByNombre(nombre);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/{id}/vehiculos-count")
    @Operation(summary = "Contar vehículos por marca", description = "Retorna la cantidad de vehículos asociados a una marca")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<Long>> countVehiculosByMarca(
            @Parameter(description = "ID de la marca") @PathVariable Long id) {
        long count = marcaUseCase.countVehiculosByMarcaId(id);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}