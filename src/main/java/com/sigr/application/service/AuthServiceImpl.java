package com.sigr.application.service;

import com.sigr.application.dto.auth.LoginRequestDTO;
import com.sigr.application.dto.auth.LoginResponseDTO;
import com.sigr.application.dto.auth.RegisterRequestDTO;
import com.sigr.application.port.input.AuthUseCase;
import com.sigr.domain.entity.Rol;
import com.sigr.domain.entity.Usuario;
import com.sigr.domain.entity.UsuarioRol;
import com.sigr.domain.exception.BusinessException;
import com.sigr.domain.repository.RolRepository;
import com.sigr.domain.repository.UsuarioRepository;
import com.sigr.infrastructure.security.jwt.JwtUtil;
import com.sigr.infrastructure.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            log.debug("Attempting login for user: {}", request.getUsername());
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Recargar el usuario con todas las relaciones necesarias
            Usuario usuario = usuarioRepository.findByUsernameWithRoles(request.getUsername())
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

            String token = jwtUtil.generateToken(
                authentication, 
                userDetails.getUserId(), 
                userDetails.getNombreCompleto()
            );

            log.info("User {} logged in successfully with sede: {}", 
                request.getUsername(), 
                usuario.getSede() != null ? usuario.getSede().getNombre() : "No asignada");

            return LoginResponseDTO.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationInSeconds())
                    .user(buildUserInfo(usuario, userDetails.getRoles()))
                    .build();

        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", request.getUsername());
            throw new BusinessException("Credenciales inválidas");
        }
    }

    @Override
    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO request) {
        log.debug("Attempting to register user: {}", request.getUsername());

        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El nombre de usuario ya existe");
        }

        List<Rol> roles = rolRepository.findByIdIn(request.getRoleIds());
        if (roles.size() != request.getRoleIds().size()) {
            throw new BusinessException("Uno o más roles no existen");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEstado(true);

        Usuario savedUsuario = usuarioRepository.save(usuario);

        for (Rol rol : roles) {
            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.setUsuario(savedUsuario);
            usuarioRol.setRol(rol);
            savedUsuario.getUsuarioRoles().add(usuarioRol);
        }

        usuarioRepository.save(savedUsuario);

        log.info("User {} registered successfully", request.getUsername());

        // Hacer login automático después del registro
        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        return login(loginRequest);
    }

    @Override
    public void logout(String token) {
        // Implementar blacklist de tokens si es necesario
        log.info("User logged out");
    }

    private LoginResponseDTO.UserInfoDTO buildUserInfo(Usuario usuario, List<String> roles) {
        log.debug("Building user info for user: {}, sede: {}", 
            usuario.getUsername(), 
            usuario.getSede() != null ? usuario.getSede().getNombre() : "null");
        
        String fotoBase64 = null;
        if (usuario.getFotoUrl() != null && !usuario.getFotoUrl().isEmpty()) {
            fotoBase64 = convertImageToBase64(usuario.getFotoUrl());
        }
        
        // Construir información de la sede
        List<LoginResponseDTO.SedeInfoDTO> sedes = List.of();
        if (usuario.getSede() != null) {
            log.debug("Adding sede info: id={}, nombre={}, direccion={}", 
                usuario.getSede().getId(), 
                usuario.getSede().getNombre(), 
                usuario.getSede().getDireccion());
                
            LoginResponseDTO.SedeInfoDTO sedeInfo = LoginResponseDTO.SedeInfoDTO.builder()
                    .id(usuario.getSede().getId())
                    .nombre(usuario.getSede().getNombre())
                    .direccion(usuario.getSede().getDireccion())
                    .build();
            sedes = List.of(sedeInfo);
        } else {
            log.debug("Usuario {} no tiene sede asignada", usuario.getUsername());
        }
        
        return LoginResponseDTO.UserInfoDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombreCompleto(usuario.getNombreCompleto())
                .estado(usuario.getEstado())
                .fotoUrl(usuario.getFotoUrl())
                .fotoBase64(fotoBase64)
                .roles(roles)
                .sedes(sedes)
                .build();
    }

    private String convertImageToBase64(String fotoUrl) {
        try {
            // Remover el "/" inicial si existe para construir la ruta correcta
            String relativePath = fotoUrl.startsWith("/") ? fotoUrl.substring(1) : fotoUrl;
            Path imagePath = Paths.get(relativePath);
            
            if (!Files.exists(imagePath)) {
                log.warn("Imagen no encontrada en la ruta: {}", imagePath);
                return null;
            }
            
            byte[] imageBytes = Files.readAllBytes(imagePath);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // Detectar el tipo de imagen por la extensión
            String extension = fotoUrl.substring(fotoUrl.lastIndexOf(".") + 1).toLowerCase();
            String mimeType = getMimeType(extension);
            
            return "data:" + mimeType + ";base64," + base64Image;
            
        } catch (IOException e) {
            log.error("Error al convertir imagen a base64: {}", e.getMessage());
            return null;
        }
    }

    private String getMimeType(String extension) {
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            default:
                return "image/jpeg"; // default
        }
    }
}