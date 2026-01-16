package com.example.conversonweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot Application
 * Conversor de Archivos a PDF - Versión Web
 */
@SpringBootApplication
@EnableAsync
public class ConversonWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConversonWebApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Conversor de Archivos a PDF - Web");
        System.out.println("  Aplicación iniciada exitosamente");
        System.out.println("  URL: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
