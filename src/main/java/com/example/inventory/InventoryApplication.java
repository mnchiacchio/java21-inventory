package com.example.inventory;

import com.example.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PreDestroy;

/**
 * Clase principal de la aplicación Spring Boot.
 * 
 * Esta clase inicia la aplicación y configura los beans necesarios.
 * Implementa el patrón de configuración centralizada de Spring Boot.
 */
@SpringBootApplication
public class InventoryApplication {

    @Autowired
    private InventoryService inventoryService;

    public static void main(String[] args) {
        // Inicia la aplicación Spring Boot
        SpringApplication.run(InventoryApplication.class, args);
        
        // Mensaje de bienvenida
        System.out.println("🚀 Inventory API iniciada correctamente!");
        System.out.println("📋 Endpoints disponibles en: http://localhost:8080/api/items");
        System.out.println("⚡ Ejecutándose con Virtual Threads de Java 21");
    }

    /**
     * Configuración CORS para permitir peticiones desde el frontend.
     * 
     * @return Configurador CORS
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    /**
     * Método que se ejecuta antes de cerrar la aplicación.
     * Cierra correctamente el ExecutorService de Virtual Threads.
     */
    @PreDestroy
    public void onShutdown() {
        System.out.println("🛑 Cerrando Inventory API...");
        inventoryService.shutdown();
        System.out.println("✅ Aplicación cerrada correctamente");
    }
}
