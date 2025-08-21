package com.sigr.infrastructure.controller;

import com.sigr.application.dto.sede.SedeRequestDTO;
import com.sigr.application.dto.sede.SedeResponseDTO;
import com.sigr.application.port.input.SedeUseCase;
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
@RequestMapping("/api/v1/sedes")
@RequiredArgsConstructor
@Tag(name = "Sedes", description = "API para gestión de sedes/ubicaciones")
@SecurityRequirement(name = "Bearer Authentication")
public class SedeController {

    private final SedeUseCase sedeUseCase;

    @GetMapping
    @Operation(summary = "Obtener todas las sedes", description = "Retorna una lista de todas las sedes registradas")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de sedes obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MECANICO', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<SedeResponseDTO>>> getAllSedes() {
        List<SedeResponseDTO> sedes = sedeUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(sedes));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener sedes paginadas", description = "Retorna una página de sedes")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<PagedResponse<SedeResponseDTO>>> getAllSedesPaginated(Pageable pageable) {
        Page<SedeResponseDTO> sedesPage = sedeUseCase.findAllPaginated(pageable);
        PagedResponse<SedeResponseDTO> pagedResponse = PagedResponse.of(sedesPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sede por ID", description = "Retorna una sede específica por su ID")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MECANICO', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<SedeResponseDTO>> getSedeById(
            @Parameter(description = "ID de la sede") @PathVariable Long id) {
        SedeResponseDTO sede = sedeUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(sede));
    }

    @PostMapping
    @Operation(summary = "Crear nueva sede", description = "Crea una nueva sede en el sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<SedeResponseDTO>> createSede(
            @Valid @RequestBody SedeRequestDTO request) {
        SedeResponseDTO sede = sedeUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(sede));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sede", description = "Actualiza una sede existente")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<SedeResponseDTO>> updateSede(
            @Parameter(description = "ID de la sede") @PathVariable Long id,
            @Valid @RequestBody SedeRequestDTO request) {
        SedeResponseDTO sede = sedeUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(sede));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sede", description = "Elimina una sede del sistema")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Void>> deleteSede(
            @Parameter(description = "ID de la sede") @PathVariable Long id) {
        sedeUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/exists")
    @Operation(summary = "Verificar si existe una sede", description = "Verifica si existe una sede con el nombre especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<Boolean>> existsByNombre(
            @Parameter(description = "Nombre de la sede") @RequestParam String nombre) {
        boolean exists = sedeUseCase.existsByNombre(nombre);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar sedes por nombre", description = "Busca sedes que contengan el texto especificado en el nombre")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<SedeResponseDTO>>> searchSedesByNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre) {
        List<SedeResponseDTO> sedes = sedeUseCase.findByNombreContaining(nombre);
        return ResponseEntity.ok(ApiResponse.success(sedes));
    }
}