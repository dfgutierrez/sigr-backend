package com.sigr.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@Service
public class BarcodeGeneratorService {

    private static final String COMPANY_PREFIX = "775"; // Prefijo de tu empresa (puedes personalizarlo)
    private static final Random random = new Random();

    /**
     * Genera un código de barras EAN-13 válido
     * Formato: {3 dígitos empresa}{4 dígitos categoría}{5 dígitos secuencial}{1 dígito verificación}
     */
    public String generateEAN13(Long categoriaId) {
        // Prefijo de empresa (3 dígitos)
        String companyPrefix = COMPANY_PREFIX;
        
        // Código de categoría (4 dígitos, rellenado con ceros)
        String categoryCode = String.format("%04d", categoriaId != null ? categoriaId : 0);
        
        // Número secuencial (5 dígitos aleatorios por simplicidad)
        String sequential = String.format("%05d", random.nextInt(100000));
        
        // Construir los primeros 12 dígitos
        String partialCode = companyPrefix + categoryCode + sequential;
        
        // Calcular dígito verificador
        String checkDigit = calculateEAN13CheckDigit(partialCode);
        
        String fullCode = partialCode + checkDigit;
        log.debug("Generated EAN-13 barcode: {}", fullCode);
        
        return fullCode;
    }

    /**
     * Genera un código interno más simple (para uso interno)
     * Formato: PROD-{YYYYMMDD}-{6 dígitos aleatorios}
     */
    public String generateInternalCode() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = String.format("%06d", random.nextInt(1000000));
        
        String code = "PROD-" + datePrefix + "-" + randomSuffix;
        log.debug("Generated internal barcode: {}", code);
        
        return code;
    }

    /**
     * Calcula el dígito verificador para códigos EAN-13
     */
    private String calculateEAN13CheckDigit(String code12) {
        if (code12.length() != 12) {
            throw new IllegalArgumentException("El código debe tener exactamente 12 dígitos");
        }
        
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(code12.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        
        int checkDigit = (10 - (sum % 10)) % 10;
        return String.valueOf(checkDigit);
    }

    /**
     * Valida un código de barras EAN-13
     */
    public boolean isValidEAN13(String barcode) {
        if (barcode == null || barcode.length() != 13 || !barcode.matches("\\d{13}")) {
            return false;
        }
        
        try {
            String code12 = barcode.substring(0, 12);
            String expectedCheckDigit = calculateEAN13CheckDigit(code12);
            String actualCheckDigit = barcode.substring(12);
            
            return expectedCheckDigit.equals(actualCheckDigit);
        } catch (Exception e) {
            log.warn("Error validating EAN-13 barcode: {}", barcode, e);
            return false;
        }
    }

    /**
     * Valida formato de código de barras (admite varios formatos)
     */
    public boolean isValidBarcodeFormat(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return false;
        }
        
        barcode = barcode.trim();
        
        // EAN-13 (13 dígitos)
        if (barcode.matches("\\d{13}")) {
            return isValidEAN13(barcode);
        }
        
        // EAN-8 (8 dígitos)
        if (barcode.matches("\\d{8}")) {
            return true; // Simplificado, podrías agregar validación EAN-8
        }
        
        // UPC-A (12 dígitos)
        if (barcode.matches("\\d{12}")) {
            return true;
        }
        
        // Código interno (formato PROD-YYYYMMDD-XXXXXX)
        if (barcode.matches("PROD-\\d{8}-\\d{6}")) {
            return true;
        }
        
        // Código alfanumérico personalizado (entre 6 y 50 caracteres)
        if (barcode.matches("[A-Za-z0-9-_]{6,50}")) {
            return true;
        }
        
        return false;
    }
}