-- Script para limpiar menús existentes y cargar los de Notus
-- EJECUTAR ESTE SCRIPT EN TU BASE DE DATOS

-- Limpiar datos existentes
DELETE FROM menu_rol;
DELETE FROM menu;

-- Resetear secuencias (para PostgreSQL)
-- Si usas PostgreSQL, descomenta estas líneas:
-- ALTER SEQUENCE menu_id_seq RESTART WITH 1;

-- Si usas MySQL, descomenta esta línea:
-- ALTER TABLE menu AUTO_INCREMENT = 1;

-- Insertar menús de Notus
INSERT INTO menu (nombre, ruta, icono, categoria, orden) VALUES 
-- Admin Layout (Menús principales de Notus)
('Dashboard', '/admin/dashboard', 'fas fa-tv', 'Admin Layout', 1),
('Settings', '/admin/settings', 'fas fa-tools', 'Admin Layout', 2),
('Tables', '/admin/tables', 'fas fa-table', 'Admin Layout', 3),
('Maps', '/admin/maps', 'fas fa-map-marked', 'Admin Layout', 4),

-- Auth Layout
('Landing', '/landing', 'fas fa-rocket', 'Auth Layout', 1),
('Profile', '/profile', 'fas fa-user-circle', 'Auth Layout', 2),

-- No Layout
('Login', '/auth/login', 'fas fa-fingerprint', 'No Layout', 1),
('Register', '/auth/register', 'fas fa-clipboard-list', 'No Layout', 2),

-- SIGR (Funcionalidades del sistema)
('Usuarios', '/admin/usuarios', 'fas fa-users', 'SIGR', 1),
('Vehículos', '/admin/vehiculos', 'fas fa-car', 'SIGR', 2),
('Inventario', '/admin/inventario', 'fas fa-boxes', 'SIGR', 3),
('Reportes', '/admin/reportes', 'fas fa-chart-bar', 'SIGR', 4),
('Administrar Menús', '/admin/menus', 'fas fa-sitemap', 'SIGR', 5);

-- Asignar menús a roles (asumiendo roles: 1=ADMINISTRADOR, 2=USER, 3=MECANICO)

-- Dashboard - todos los roles
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(1, 1), (1, 2), (1, 3);

-- Settings - solo administrador
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(2, 1);

-- Tables - administrador y user
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(3, 1), (3, 2);

-- Maps - administrador y user
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(4, 1), (4, 2);

-- Landing - todos los roles
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(5, 1), (5, 2), (5, 3);

-- Profile - todos los roles
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(6, 1), (6, 2), (6, 3);

-- Login - todos los roles
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(7, 1), (7, 2), (7, 3);

-- Register - solo administrador
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(8, 1);

-- Usuarios - solo administrador
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(9, 1);

-- Vehículos - todos los roles
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(10, 1), (10, 2), (10, 3);

-- Inventario - administrador y mecánico
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(11, 1), (11, 3);

-- Reportes - administrador y user
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(12, 1), (12, 2);

-- Administrar Menús - solo administrador
INSERT INTO menu_rol (menu_id, rol_id) VALUES 
(13, 1);

-- Verificar resultados
SELECT 'Menús de Notus cargados exitosamente. Total de menús:', COUNT(*) FROM menu;
SELECT 'Total de asignaciones rol-menú:', COUNT(*) FROM menu_rol;