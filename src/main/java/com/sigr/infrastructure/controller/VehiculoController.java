package com.sigr.infrastructure.controller;

import com.sigr.application.dto.vehiculo.VehiculoRequestDTO;
import com.sigr.application.dto.vehiculo.VehiculoResponseDTO;
import com.sigr.application.dto.vehiculo.VehiculoUpdateDTO;
import com.sigr.application.dto.vehiculo.VehiculoSearchResultDTO;
import com.sigr.application.port.input.VehiculoUseCase;
import com.sigr.infrastructure.common.response.ApiResponse;
import com.sigr.infrastructure.common.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehículos", description = "API para gestión de vehículos")
public class VehiculoController {

    private final VehiculoUseCase vehiculoUseCase;

    @GetMapping
    @Operation(summary = "Obtener todos los vehículos", description = "Retorna una lista de todos los vehículos registrados")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de vehículos obtenida exitosamente")
    })
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<List<VehiculoResponseDTO>>> getAllVehiculos() {
        List<VehiculoResponseDTO> vehiculos = vehiculoUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(vehiculos));
    }

    @GetMapping("/paginated")
    @Operation(summary = "Obtener vehículos paginados", description = "Retorna una página de vehículos")
    public ResponseEntity<ApiResponse<PagedResponse<VehiculoResponseDTO>>> getAllVehiculosPaginated(Pageable pageable) {
        Page<VehiculoResponseDTO> vehiculosPage = vehiculoUseCase.findAllPaginated(pageable);
        PagedResponse<VehiculoResponseDTO> pagedResponse = PagedResponse.of(vehiculosPage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vehículo por ID", description = "Retorna un vehículo específico por su ID")
    public ResponseEntity<ApiResponse<VehiculoResponseDTO>> getVehiculoById(
            @Parameter(description = "ID del vehículo") @PathVariable Long id) {
        VehiculoResponseDTO vehiculo = vehiculoUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(vehiculo));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo vehículo", description = "Crea un nuevo vehículo en el sistema")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<VehiculoResponseDTO>> createVehiculo(
            @Valid @RequestBody VehiculoRequestDTO request) {
        VehiculoResponseDTO vehiculo = vehiculoUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(vehiculo, "Vehículo creado exitosamente"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vehículo", description = "Actualiza un vehículo existente")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<VehiculoResponseDTO>> updateVehiculo(
            @Parameter(description = "ID del vehículo") @PathVariable Long id,
            @Valid @RequestBody VehiculoUpdateDTO request) {
        VehiculoResponseDTO vehiculo = vehiculoUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(vehiculo, "Vehículo actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vehículo", description = "Elimina un vehículo del sistema")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> deleteVehiculo(
            @Parameter(description = "ID del vehículo") @PathVariable Long id) {
        vehiculoUseCase.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Vehículo eliminado exitosamente"));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Obtener vehículos por tipo", description = "Retorna vehículos filtrados por tipo")
    public ResponseEntity<ApiResponse<List<VehiculoResponseDTO>>> getVehiculosByTipo(
            @Parameter(description = "Tipo de vehículo (moto/carro)") @PathVariable String tipo) {
        List<VehiculoResponseDTO> vehiculos = vehiculoUseCase.findByTipo(tipo);
        return ResponseEntity.ok(ApiResponse.success(vehiculos));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar vehículos por placa", description = "Busca vehículos que contengan el texto en la placa")
    public ResponseEntity<ApiResponse<List<VehiculoResponseDTO>>> searchVehiculosByPlaca(
            @Parameter(description = "Texto a buscar en la placa") @RequestParam String placa) {
        List<VehiculoResponseDTO> vehiculos = vehiculoUseCase.findByPlacaContaining(placa);
        return ResponseEntity.ok(ApiResponse.success(vehiculos));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener vehículos por estado", description = "Retorna vehículos filtrados por estado (activo/inactivo)")
    public ResponseEntity<ApiResponse<List<VehiculoResponseDTO>>> getVehiculosByEstado(
            @Parameter(description = "Estado del vehículo (true/false)") @PathVariable Boolean estado) {
        List<VehiculoResponseDTO> vehiculos = vehiculoUseCase.findByEstado(estado);
        return ResponseEntity.ok(ApiResponse.success(vehiculos));
    }

    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "Obtener vehículos por sede", description = "Retorna vehículos de una sede específica")
    public ResponseEntity<ApiResponse<List<VehiculoResponseDTO>>> getVehiculosBySede(
            @Parameter(description = "ID de la sede") @PathVariable Long sedeId) {
        List<VehiculoResponseDTO> vehiculos = vehiculoUseCase.findBySede(sedeId);
        return ResponseEntity.ok(ApiResponse.success(vehiculos));
    }

    @GetMapping("/search-for-sale/{placa}")
    @Operation(
        summary = "Buscar vehículo para venta", 
        description = "Busca un vehículo por placa para el módulo de ventas. Si no se encuentra, indica que se puede registrar."
    )
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<VehiculoSearchResultDTO>> searchVehiculoForSale(
            @Parameter(description = "Placa del vehículo a buscar") @PathVariable String placa) {
        VehiculoSearchResultDTO result = vehiculoUseCase.searchByPlaca(placa);
        return ResponseEntity.ok(ApiResponse.success(result));
    }


    @GetMapping("/placa/{placa}")
    @Operation(summary = "Obtener vehículo por placa", description = "Retorna un vehículo específico por su placa")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ApiResponse<VehiculoResponseDTO>> getVehiculoByPlaca(
            @Parameter(description = "Placa del vehículo") @PathVariable String placa) {
        VehiculoResponseDTO vehiculo = vehiculoUseCase.findByPlaca(placa);
        return ResponseEntity.ok(ApiResponse.success(vehiculo));
    }
}