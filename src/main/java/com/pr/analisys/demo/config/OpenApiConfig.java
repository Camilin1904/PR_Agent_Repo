package com.pr.analisys.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * OpenAPI configuration for Swagger documentation
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce Backend API")
                        .description("A comprehensive REST API for e-commerce operations including user management, product catalog, and order processing. " +
                                   "Built with Spring Boot, JPA, H2 Database, and documented with OpenAPI/Swagger.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("support@ecommerce-demo.com")
                                .url("https://github.com/example/ecommerce-backend"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local development server"),
                        new Server()
                                .url("https://api.ecommerce-demo.com")
                                .description("Production server")
                ));
    }
}