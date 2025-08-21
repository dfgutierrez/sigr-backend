package com.sigr.infrastructure.controller;

import com.sigr.application.dto.sql.SqlQueryRequestDTO;
import com.sigr.application.dto.sql.SqlQueryResponseDTO;
import com.sigr.application.port.in.SqlQueryUseCase;
import com.sigr.infrastructure.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sql")
@RequiredArgsConstructor
@Tag(name = "SQL Query Executor", description = "API para ejecutar consultas SQL directas (Solo Administradores)")
@SecurityRequirement(name = "Bearer Authentication")
public class SqlQueryController {

    private final SqlQueryUseCase sqlQueryUseCase;

    @PostMapping("/execute")
    @Operation(
        summary = "Ejecutar consulta SQL", 
        description = "Ejecuta una consulta SQL directa en la base de datos. Soporta SELECT, INSERT, UPDATE, DELETE. Restringido solo a administradores."
    )
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<SqlQueryResponseDTO>> executeQuery(
            @Valid @RequestBody SqlQueryRequestDTO request) {
        
        SqlQueryResponseDTO result = sqlQueryUseCase.executeQuery(request);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result));
        } else {
            // Para errores, devolvemos el resultado completo pero con status de error
            return ResponseEntity.badRequest().body(
                ApiResponse.<SqlQueryResponseDTO>builder()
                    .success(false)
                    .error(result.getMessage())
                    .data(result)
                    .build()
            );
        }
    }

    @PostMapping("/validate")
    @Operation(
        summary = "Validar consulta SQL", 
        description = "Valida si una consulta SQL es segura para ejecutar sin ejecutarla"
    )
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateQuery(
            @Valid @RequestBody SqlQueryRequestDTO request) {
        
        boolean isSafe = sqlQueryUseCase.isQuerySafe(request.getQuery());
        
        Map<String, Object> response = Map.of(
            "query", request.getQuery(),
            "isSafe", isSafe,
            "message", isSafe ? "Consulta válida" : "Consulta no permitida por razones de seguridad"
        );
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/examples")
    @Operation(
        summary = "Obtener ejemplos de consultas SQL", 
        description = "Retorna ejemplos de consultas SQL comunes para el sistema"
    )
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getQueryExamples() {
        
        Map<String, List<String>> examples = Map.of(
            "SELECT", List.of(
                "SELECT * FROM venta WHERE fecha >= '2024-01-01' LIMIT 10;",
                "SELECT s.nombre, COUNT(v.id) as total_ventas FROM sede s LEFT JOIN venta v ON s.id = v.sede_id GROUP BY s.id, s.nombre;",
                "SELECT p.nombre, SUM(dv.cantidad) as total_vendido FROM producto p JOIN detalle_venta dv ON p.id = dv.producto_id GROUP BY p.id ORDER BY total_vendido DESC LIMIT 5;"
            ),
            "INSERT", List.of(
                "INSERT INTO categoria (nombre, descripcion, estado) VALUES ('Nueva Categoría', 'Descripción', true);",
                "INSERT INTO marca (nombre, estado) VALUES ('Nueva Marca', true);"
            ),
            "UPDATE", List.of(
                "UPDATE producto SET precio_venta = precio_venta * 1.1 WHERE categoria_id = 1;",
                "UPDATE usuario SET estado = false WHERE id = 5;"
            ),
            "DELETE", List.of(
                "DELETE FROM venta WHERE estado = false AND fecha < '2023-01-01';",
                "DELETE FROM inventario WHERE cantidad = 0 AND sede_id = 1;"
            )
        );
        
        return ResponseEntity.ok(ApiResponse.success(examples));
    }
}