package com.sigr.domain.exception;

public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s no encontrado con %s: %s", resource, field, value));
    }
}