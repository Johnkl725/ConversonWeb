package com.example.conversonweb.application.service;

import com.example.conversonweb.domain.exception.ConversionException;
import com.example.conversonweb.domain.model.ConversionResult;
import com.example.conversonweb.domain.model.ConversionType;
import com.example.conversonweb.domain.service.FileConverter;
import com.example.conversonweb.infrastructure.converter.MergeImagesToPdfConverter;
import com.example.conversonweb.infrastructure.converter.MergeWordsToPdfConverter;
import com.example.conversonweb.infrastructure.factory.ConverterFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main conversion service with async support and WebSocket progress updates
 * Supports both individual conversions and merge operations
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ConversionService {

    private final ConverterFactory converterFactory;
    private final FileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Converts files asynchronously with progress updates via WebSocket
     * Handles both individual and merge operations
     */
    @Async
    public CompletableFuture<ConversionResult> convertFilesAsync(
            List<File> sourceFiles,
            ConversionType conversionType,
            String jobId) {

        log.info("Starting async conversion job {} with {} files (type: {})",
                jobId, sourceFiles.size(), conversionType);

        try {
            File outputDir = fileStorageService.getOutputDirectory(jobId);

            // Check if it's a merge operation
            if (conversionType.isMergeOperation()) {
                return CompletableFuture.completedFuture(
                        executeMergeConversion(sourceFiles, conversionType, outputDir, jobId));
            } else {
                return CompletableFuture.completedFuture(
                        executeIndividualConversion(sourceFiles, conversionType, outputDir, jobId));
            }

        } catch (Exception e) {
            log.error("Error in conversion job {}", jobId, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Executes merge conversion: multiple files -> ONE PDF
     */
    private ConversionResult executeMergeConversion(List<File> sourceFiles, ConversionType conversionType,
            File outputDir, String jobId) {
        try {
            String outputFileName = "merged_" + System.currentTimeMillis() + ".pdf";
            File outputFile = new File(outputDir, outputFileName);

            sendProgressUpdate(jobId, 0, sourceFiles.size(), "Iniciando combinación...", "processing");

            // Use appropriate merge converter
            if (conversionType == ConversionType.MERGE_IMAGES_TO_PDF) {
                log.info("Merging {} images into single PDF", sourceFiles.size());
                MergeImagesToPdfConverter converter = new MergeImagesToPdfConverter();
                converter.convertMultipleToPdf(sourceFiles, outputFile);
            } else if (conversionType == ConversionType.MERGE_WORDS_TO_PDF) {
                log.info("Merging {} Word documents into single PDF", sourceFiles.size());
                MergeWordsToPdfConverter converter = new MergeWordsToPdfConverter();
                converter.convertMultipleToPdf(sourceFiles, outputFile);
            }

            sendProgressUpdate(jobId, sourceFiles.size(), sourceFiles.size(),
                    "Combinación completada", "success");

            ConversionResult result = ConversionResult.builder()
                    .success(true)
                    .successCount(sourceFiles.size())
                    .failureCount(0)
                    .convertedFiles(List.of(outputFile))
                    .errors(new ArrayList<>())
                    .build();

            sendCompletionUpdate(jobId, result);
            return result;

        } catch (Exception e) {
            log.error("Error in merge conversion", e);
            ConversionResult result = ConversionResult.builder()
                    .success(false)
                    .successCount(0)
                    .failureCount(sourceFiles.size())
                    .convertedFiles(new ArrayList<>())
                    .errors(List.of("Error al combinar archivos: " + e.getMessage()))
                    .build();
            sendCompletionUpdate(jobId, result);
            return result;
        }
    }

    /**
     * Executes individual conversion: multiple files -> multiple PDFs
     */
    private ConversionResult executeIndividualConversion(List<File> sourceFiles, ConversionType conversionType,
            File outputDir, String jobId) {
        FileConverter converter = converterFactory.createConverter(conversionType);

        int successCount = 0;
        int failureCount = 0;
        List<File> convertedFiles = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < sourceFiles.size(); i++) {
            File sourceFile = sourceFiles.get(i);
            String fileName = sourceFile.getName();

            // Send progress update via WebSocket
            sendProgressUpdate(jobId, i + 1, sourceFiles.size(), fileName, "processing");

            try {
                String outputFileName = generateOutputFileName(fileName);
                File outputFile = new File(outputDir, outputFileName);

                converter.convertToPdf(sourceFile, outputFile);

                successCount++;
                convertedFiles.add(outputFile);

                sendProgressUpdate(jobId, i + 1, sourceFiles.size(), fileName, "success");
                log.info("Successfully converted: {}", fileName);

            } catch (ConversionException e) {
                failureCount++;
                String errorMsg = String.format("%s: %s", fileName, e.getMessage());
                errors.add(errorMsg);

                sendProgressUpdate(jobId, i + 1, sourceFiles.size(), fileName, "failed");
                log.error("Failed to convert: {}", fileName, e);
            }
        }

        ConversionResult result = ConversionResult.builder()
                .success(failureCount == 0)
                .successCount(successCount)
                .failureCount(failureCount)
                .convertedFiles(convertedFiles)
                .errors(errors)
                .build();

        // Send completion message
        sendCompletionUpdate(jobId, result);

        return result;
    }

    private void sendProgressUpdate(String jobId, int current, int total, String fileName, String status) {
        var message = new ProgressMessage(jobId, current, total, fileName, status,
                (int) ((current * 100.0) / total));
        messagingTemplate.convertAndSend("/topic/progress/" + jobId, message);
    }

    private void sendCompletionUpdate(String jobId, ConversionResult result) {
        var message = new CompletionMessage(jobId, result.getSummary(),
                result.getSuccessCount(), result.getFailureCount());
        messagingTemplate.convertAndSend("/topic/completion/" + jobId, message);
    }

    private String generateOutputFileName(String sourceFileName) {
        int lastDot = sourceFileName.lastIndexOf('.');
        String baseName = lastDot > 0 ? sourceFileName.substring(0, lastDot) : sourceFileName;
        return baseName + ".pdf";
    }

    // Inner classes for WebSocket messages
    record ProgressMessage(String jobId, int current, int total, String fileName,
            String status, int progress) {
    }

    record CompletionMessage(String jobId, String message, int successCount, int failureCount) {
    }
}
