package com.example.conversonweb.presentation.controller;

import com.example.conversonweb.application.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * Controller for downloading converted PDF files
 */
@RestController
@RequestMapping("/api/download")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DownloadController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{jobId}/{filename}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String jobId,
            @PathVariable String filename) {

        try {
            File file = fileStorageService.getOutputFile(jobId, filename);

            if (!file.exists()) {
                log.warn("File not found: {}/{}", jobId, filename);
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error downloading file: {}/{}", jobId, filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
