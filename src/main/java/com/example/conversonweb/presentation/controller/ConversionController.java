package com.example.conversonweb.presentation.controller;

import com.example.conversonweb.application.dto.ConversionRequestDto;
import com.example.conversonweb.application.dto.ConversionResponseDto;
import com.example.conversonweb.application.service.ConversionService;
import com.example.conversonweb.domain.model.ConversionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * REST Controller for file conversion operations
 */
@RestController
@RequestMapping("/api/conversion")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConversionController {

    private final ConversionService conversionService;
    private final FileUploadController fileUploadController;

    // Store job results
    private final Map<String, ConversionResult> jobResults = new ConcurrentHashMap<>();
    private final Map<String, String> jobStatus = new ConcurrentHashMap<>();

    @PostMapping("/start")
    public ResponseEntity<?> startConversion(@Valid @RequestBody ConversionRequestDto request) {
        try {
            String jobId = UUID.randomUUID().toString();

            // Get uploaded files
            List<File> files = request.getFileIds().stream()
                    .map(fileUploadController::getUploadedFile)
                    .filter(java.util.Objects::nonNull)
                    .toList();

            if (files.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "No se encontraron archivos válidos"));
            }

            // Mark job as processing
            jobStatus.put(jobId, "PROCESSING");

            // Start async conversion
            conversionService.convertFilesAsync(files, request.getConversionType(), jobId)
                    .thenAccept(result -> {
                        jobResults.put(jobId, result);
                        jobStatus.put(jobId, "COMPLETED");
                        // Clean up uploaded files
                        fileUploadController.clearUploadedFiles(request.getFileIds());
                    })
                    .exceptionally(ex -> {
                        log.error("Conversion job {} failed", jobId, ex);
                        jobStatus.put(jobId, "FAILED");
                        return null;
                    });

            ConversionResponseDto response = ConversionResponseDto.builder()
                    .jobId(jobId)
                    .status("PROCESSING")
                    .totalFiles(files.size())
                    .progress(0)
                    .message("Conversión iniciada")
                    .build();

            log.info("Started conversion job {} with {} files", jobId, files.size());

            return ResponseEntity.accepted().body(response);

        } catch (Exception e) {
            log.error("Error starting conversion", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<?> getStatus(@PathVariable String jobId) {
        String status = jobStatus.get(jobId);

        if (status == null) {
            return ResponseEntity.notFound().build();
        }

        ConversionResult result = jobResults.get(jobId);

        ConversionResponseDto response = ConversionResponseDto.builder()
                .jobId(jobId)
                .status(status)
                .build();

        if (result != null) {
            response.setSuccessCount(result.getSuccessCount());
            response.setFailureCount(result.getFailureCount());
            response.setProgress(100);
            response.setMessage(result.getSummary());
            response.setConvertedFiles(result.getConvertedFiles().stream()
                    .map(File::getName)
                    .toList());
            response.setErrors(result.getErrors());
        }

        return ResponseEntity.ok(response);
    }
}
