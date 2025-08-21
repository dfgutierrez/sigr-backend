package com.sigr.application.dto.sql;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class SqlQueryResponseDTO {
    
    private String queryType; // SELECT, INSERT, UPDATE, DELETE, etc.
    private String executedQuery;
    private Integer rowsAffected; // Para INSERT, UPDATE, DELETE
    private List<String> columns; // Para SELECT
    private List<Map<String, Object>> data; // Para SELECT
    private Long executionTimeMs;
    private LocalDateTime executedAt;
    private String message;
    private boolean success;
    
    public static SqlQueryResponseDTO success(String queryType, String query) {
        SqlQueryResponseDTO response = new SqlQueryResponseDTO();
        response.setSuccess(true);
        response.setQueryType(queryType);
        response.setExecutedQuery(query);
        response.setExecutedAt(LocalDateTime.now());
        return response;
    }
    
    public static SqlQueryResponseDTO error(String query, String message) {
        SqlQueryResponseDTO response = new SqlQueryResponseDTO();
        response.setSuccess(false);
        response.setExecutedQuery(query);
        response.setMessage(message);
        response.setExecutedAt(LocalDateTime.now());
        return response;
    }
}