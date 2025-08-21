package com.sigr.application.service;

import com.sigr.application.dto.sql.SqlQueryRequestDTO;
import com.sigr.application.dto.sql.SqlQueryResponseDTO;
import com.sigr.application.port.in.SqlQueryUseCase;
import com.sigr.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqlQueryServiceImpl implements SqlQueryUseCase {

    private final JdbcTemplate jdbcTemplate;
    
    // Patrones para detectar tipos de consultas peligrosas
    private static final List<Pattern> DANGEROUS_PATTERNS = Arrays.asList(
        Pattern.compile("\\bDROP\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bTRUNCATE\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bCREATE\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bALTER\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bGRANT\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bREVOKE\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bEXEC\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bEXECUTE\\s*\\(", Pattern.CASE_INSENSITIVE)
    );
    
    // Patrones para detectar tipos de consulta
    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern INSERT_PATTERN = Pattern.compile("^\\s*INSERT\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE_PATTERN = Pattern.compile("^\\s*UPDATE\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELETE_PATTERN = Pattern.compile("^\\s*DELETE\\s+", Pattern.CASE_INSENSITIVE);

    @Override
    @Transactional
    public SqlQueryResponseDTO executeQuery(SqlQueryRequestDTO request) {
        long startTime = System.currentTimeMillis();
        String query = request.getQuery().trim();
        
        try {
            // Validar consulta
            if (!isQuerySafe(query)) {
                return SqlQueryResponseDTO.error(query, "Consulta no permitida por razones de seguridad");
            }
            
            String queryType = detectQueryType(query);
            SqlQueryResponseDTO response = SqlQueryResponseDTO.success(queryType, query);
            
            if ("SELECT".equals(queryType)) {
                executeSelectQuery(query, request.getMaxRows(), response);
            } else {
                executeModificationQuery(query, response);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            response.setExecutionTimeMs(executionTime);
            response.setMessage("Consulta ejecutada exitosamente");
            
            log.info("SQL Query executed: {} | Type: {} | Time: {}ms", 
                    query.substring(0, Math.min(100, query.length())), queryType, executionTime);
            
            return response;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Error executing SQL query: {}", query, e);
            
            SqlQueryResponseDTO errorResponse = SqlQueryResponseDTO.error(query, e.getMessage());
            errorResponse.setExecutionTimeMs(executionTime);
            return errorResponse;
        }
    }
    
    private void executeSelectQuery(String query, Integer maxRows, SqlQueryResponseDTO response) {
        // Agregar LIMIT si no existe y maxRows está definido
        String limitedQuery = query;
        if (maxRows != null && maxRows > 0 && !query.toLowerCase().contains("limit")) {
            limitedQuery = query + " LIMIT " + maxRows;
        }
        
        List<Map<String, Object>> results = jdbcTemplate.query(limitedQuery, (rs, rowNum) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                row.put(columnName, value);
            }
            return row;
        });
        
        // Extraer nombres de columnas
        List<String> columns = new ArrayList<>();
        if (!results.isEmpty()) {
            columns.addAll(results.get(0).keySet());
        }
        
        response.setColumns(columns);
        response.setData(results);
        response.setRowsAffected(results.size());
    }
    
    private void executeModificationQuery(String query, SqlQueryResponseDTO response) {
        int rowsAffected = jdbcTemplate.update(query);
        response.setRowsAffected(rowsAffected);
    }
    
    private String detectQueryType(String query) {
        if (SELECT_PATTERN.matcher(query).find()) return "SELECT";
        if (INSERT_PATTERN.matcher(query).find()) return "INSERT";
        if (UPDATE_PATTERN.matcher(query).find()) return "UPDATE";
        if (DELETE_PATTERN.matcher(query).find()) return "DELETE";
        return "OTHER";
    }
    
    @Override
    public boolean isQuerySafe(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        
        // Verificar patrones peligrosos
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(query).find()) {
                return false;
            }
        }
        
        // Verificar que no contenga múltiples statements (inyección SQL básica)
        if (query.contains(";") && !query.trim().endsWith(";")) {
            return false;
        }
        
        return true;
    }
}