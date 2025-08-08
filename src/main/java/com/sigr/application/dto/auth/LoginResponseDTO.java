package com.sigr.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para login")
public class LoginResponseDTO {

    @Schema(description = "Token JWT de acceso", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Tipo de token", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Tiempo de expiración en segundos", example = "3600")
    private Long expiresIn;

    @Schema(description = "Información del usuario autenticado")
    private UserInfoDTO user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información del usuario")
    public static class UserInfoDTO {
        
        @Schema(description = "ID del usuario", example = "1")
        private Long id;
        
        @Schema(description = "Nombre de usuario", example = "admin")
        private String username;
        
        @Schema(description = "Nombre completo", example = "Administrador Sistema")
        private String nombreCompleto;
        
        @Schema(description = "Estado del usuario", example = "true")
        private Boolean estado;
        
        @Schema(description = "URL de la foto del usuario", example = "/uploads/users/user_1_abc123.jpg")
        private String fotoUrl;
        
        @Schema(description = "Foto del usuario en base64", example = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ...")
        private String fotoBase64;
        
        @Schema(description = "Lista de roles del usuario")
        private List<String> roles;
        
        @Schema(description = "Lista de sedes asociadas al usuario")
        private List<SedeInfoDTO> sedes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información de sede")
    public static class SedeInfoDTO {
        
        @Schema(description = "ID de la sede", example = "1")
        private Long id;
        
        @Schema(description = "Nombre de la sede", example = "Sede Principal")
        private String nombre;
        
        @Schema(description = "Dirección de la sede", example = "Calle 123 #45-67")
        private String direccion;
    }
}