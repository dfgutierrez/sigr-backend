package com.sigr.application.port.in;

import com.sigr.application.dto.sql.SqlQueryRequestDTO;
import com.sigr.application.dto.sql.SqlQueryResponseDTO;

public interface SqlQueryUseCase {
    
    /**
     * Ejecuta una consulta SQL genérica
     * @param request La consulta SQL a ejecutar
     * @return Respuesta con los resultados o información de ejecución
     */
    SqlQueryResponseDTO executeQuery(SqlQueryRequestDTO request);
    
    /**
     * Verifica si una consulta es segura para ejecutar
     * @param query La consulta SQL a validar
     * @return true si la consulta es considerada segura
     */
    boolean isQuerySafe(String query);
}