package com.sigr.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort);
        devServer.setDescription("Servidor de Desarrollo");

        Contact contact = new Contact();
        contact.setEmail("desarrollo@sigr.com");
        contact.setName("Equipo SIGR");

        License license = new License()
                .name("Licencia Propietaria")
                .url("https://sigr.com/license");

        Info info = new Info()
                .title("SIGR Backend API")
                .version("1.0.0")
                .contact(contact)
                .description("API REST para el Sistema de Inventario Grupo Robayo (SIGR). " +
                           "Esta API proporciona endpoints para la gestión de inventarios, vehículos, " +
                           "usuarios, roles y demás funcionalidades del sistema.")
                .termsOfService("https://sigr.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authentication")));
    }
}