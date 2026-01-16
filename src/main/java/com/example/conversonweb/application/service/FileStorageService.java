package com.example.conversonweb.application.service;

import com.example.conversonweb.config.StorageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service for managing file storage (uploads and converted files)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    private final StorageConfig storageConfig;
    private Path uploadLocation;
    private Path outputLocation;

    @PostConstruct
    public void init() {
        try {
            uploadLocation = Paths.get(storageConfig.getUploadDir()).toAbsolutePath().normalize();
            outputLocation = Paths.get(storageConfig.getOutputDir()).toAbsolutePath().normalize();

            Files.createDirectories(uploadLocation);
            Files.createDirectories(outputLocation);

            log.info("Storage locations initialized:");
            log.info("  Uploads: {}", uploadLocation);
            log.info("  Output:  {}", outputLocation);

        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directories", e);
        }
    }

    /**
     * Stores an uploaded file
     */
    public File storeUploadedFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String storedFilename = UUID.randomUUID().toString() + extension;

        Path targetLocation = uploadLocation.resolve(storedFilename);
        file.transferTo(targetLocation);

        log.debug("Stored uploaded file: {} as {}", originalFilename, storedFilename);
        return targetLocation.toFile();
    }

    /**
     * Gets or creates output directory for a conversion job
     */
    public File getOutputDirectory(String jobId) throws IOException {
        Path jobOutputDir = outputLocation.resolve(jobId);
        Files.createDirectories(jobOutputDir);
        return jobOutputDir.toFile();
    }

    /**
     * Gets a file from output directory
     */
    public File getOutputFile(String jobId, String filename) {
        return outputLocation.resolve(jobId).resolve(filename).toFile();
    }

    /**
     * Deletes a file
     */
    public void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
            log.debug("Deleted file: {}", file.getName());
        }
    }

    /**
     * Gets upload directory
     */
    public Path getUploadLocation() {
        return uploadLocation;
    }

    /**
     * Gets output directory
     */
    public Path getOutputLocation() {
        return outputLocation;
    }

    private String getFileExtension(String filename) {
        if (filename == null)
            return "";
        int lastDot = filename.lastIndexOf('.');
        return (lastDot > 0) ? filename.substring(lastDot) : "";
    }
}
