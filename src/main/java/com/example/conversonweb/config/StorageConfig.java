package com.example.conversonweb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * Storage configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Data
public class StorageConfig {
    private String uploadDir = "./uploads";
    private String outputDir = "./converted";
    private int cleanupHours = 24;
}
