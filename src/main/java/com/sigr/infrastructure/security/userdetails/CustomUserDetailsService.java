package com.sigr.infrastructure.security.userdetails;

import com.sigr.domain.entity.Usuario;
import com.sigr.domain.entity.UsuarioRol;
import com.sigr.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        Usuario usuario = usuarioRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<String> roles = usuario.getUsuarioRoles().stream()
                .map(UsuarioRol::getRol)
                .map(rol -> rol.getNombre())
                .toList();

        log.debug("User {} loaded with roles: {}", username, roles);
        
        return new CustomUserDetails(usuario, roles);
    }
}