package com.sigr.infrastructure.config;

import com.sigr.domain.entity.*;
import com.sigr.domain.repository.MenuRepository;
import com.sigr.domain.repository.RolRepository;
import com.sigr.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final MenuRepository menuRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initializeRoles();
        initializeAdminUser();
        initializeMenus();
    }

    private void initializeRoles() {
        if (rolRepository.count() == 0) {
            log.info("Initializing default roles...");
            
            Rol adminRole = new Rol();
            adminRole.setNombre("ADMINISTRADOR");
            rolRepository.save(adminRole);
            
            Rol userRole = new Rol();
            userRole.setNombre("USER");
            rolRepository.save(userRole);
            
            Rol mecanicoRole = new Rol();
            mecanicoRole.setNombre("MECANICO");
            rolRepository.save(mecanicoRole);
            
            log.info("Default roles created successfully");
        }
    }

    private void initializeAdminUser() {
        if (!usuarioRepository.existsByUsername("admin")) {
            log.info("Creating default admin user...");
            
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombreCompleto("Administrador del Sistema");
            admin.setEstado(true);
            
            Usuario savedAdmin = usuarioRepository.save(admin);
            
            // Asignar rol ADMIN
            Rol adminRole = rolRepository.findById(1L).orElseThrow();
            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.setUsuario(savedAdmin);
            usuarioRol.setRol(adminRole);
            savedAdmin.getUsuarioRoles().add(usuarioRol);
            
            usuarioRepository.save(savedAdmin);
            
            log.info("Default admin user created - Username: admin, Password: admin123");
        }
    }

    private void initializeMenus() {
        // Forzar recreación de menús de Notus - cambiar a 'true' para forzar
        boolean forceRecreateMenus = false; // Cambiar a true para recrear menús
        
        if (menuRepository.count() == 0 || forceRecreateMenus) {
            if (forceRecreateMenus && menuRepository.count() > 0) {
                log.info("Forzando recreación de menús...");
                // Aquí podrías limpiar los menús existentes si es necesario
                // menuRepository.deleteAll();
            }
            log.info("Initializing default Notus menus...");
            
            Rol adminRole = rolRepository.findById(1L).orElseThrow();
            Rol userRole = rolRepository.findById(2L).orElseThrow();
            Rol mecanicoRole = rolRepository.findById(3L).orElseThrow();
            
            // Menús principales de Notus
            Menu dashboard = createMenu("Dashboard", "/admin/dashboard", "fas fa-tv", "Admin Layout", 1);
            assignRolesToMenu(dashboard, List.of(adminRole, userRole, mecanicoRole));
            
            Menu settings = createMenu("Settings", "/admin/settings", "fas fa-tools", "Admin Layout", 2);
            assignRolesToMenu(settings, List.of(adminRole));
            
            Menu tables = createMenu("Tables", "/admin/tables", "fas fa-table", "Admin Layout", 3);
            assignRolesToMenu(tables, List.of(adminRole, userRole));
            
            Menu maps = createMenu("Maps", "/admin/maps", "fas fa-map-marked", "Admin Layout", 4);
            assignRolesToMenu(maps, List.of(adminRole, userRole));
            
            // Menús de Auth Layout
            Menu landing = createMenu("Landing", "/landing", "fas fa-rocket", "Auth Layout", 1);
            assignRolesToMenu(landing, List.of(adminRole, userRole, mecanicoRole));
            
            Menu profile = createMenu("Profile", "/profile", "fas fa-user-circle", "Auth Layout", 2);
            assignRolesToMenu(profile, List.of(adminRole, userRole, mecanicoRole));
            
            // Menús sin Layout
            Menu login = createMenu("Login", "/auth/login", "fas fa-fingerprint", "No Layout", 1);
            assignRolesToMenu(login, List.of(adminRole, userRole, mecanicoRole));
            
            Menu register = createMenu("Register", "/auth/register", "fas fa-clipboard-list", "No Layout", 2);
            assignRolesToMenu(register, List.of(adminRole));
            
            // Menús adicionales para el sistema SIGR
            Menu usuarios = createMenu("Usuarios", "/admin/usuarios", "fas fa-users", "SIGR", 1);
            assignRolesToMenu(usuarios, List.of(adminRole));
            
            Menu vehiculos = createMenu("Vehículos", "/admin/vehiculos", "fas fa-car", "SIGR", 2);
            assignRolesToMenu(vehiculos, List.of(adminRole, mecanicoRole, userRole));
            
            Menu inventario = createMenu("Inventario", "/admin/inventario", "fas fa-boxes", "SIGR", 3);
            assignRolesToMenu(inventario, List.of(adminRole, mecanicoRole));
            
            Menu reportes = createMenu("Reportes", "/admin/reportes", "fas fa-chart-bar", "SIGR", 4);
            assignRolesToMenu(reportes, List.of(adminRole, userRole));
            
            Menu menus = createMenu("Administrar Menús", "/admin/menus", "fas fa-sitemap", "SIGR", 5);
            assignRolesToMenu(menus, List.of(adminRole));
            
            log.info("Default Notus menus created successfully");
        }
    }
    
    private Menu createMenu(String nombre, String ruta, String icono, String categoria, Integer orden) {
        Menu menu = new Menu();
        menu.setNombre(nombre);
        menu.setRuta(ruta);
        menu.setIcono(icono);
        menu.setCategoria(categoria);
        menu.setOrden(orden);
        return menuRepository.save(menu);
    }
    
    private void assignRolesToMenu(Menu menu, List<Rol> roles) {
        for (Rol rol : roles) {
            MenuRol menuRol = new MenuRol();
            menuRol.setMenu(menu);
            menuRol.setRol(rol);
            menu.getMenuRoles().add(menuRol);
        }
        menuRepository.save(menu);
    }
}