package com.example.conversonweb.presentation.controller;

import com.example.conversonweb.application.dto.FileUploadDto;
import com.example.conversonweb.application.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for file uploads
 */
@RestController
@RequestMapping("/api/files")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final Map<String, File> uploadedFilesCache = new HashMap<>();

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            List<FileUploadDto> uploadedFiles = new ArrayList<>();

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                File storedFile = fileStorageService.storeUploadedFile(file);
                String fileId = storedFile.getName().replace(getFileExtension(file.getOriginalFilename()), "");

                // Cache the file for conversion
                uploadedFilesCache.put(fileId, storedFile);

                FileUploadDto dto = FileUploadDto.builder()
                        .id(fileId)
                        .originalName(file.getOriginalFilename())
                        .size(file.getSize())
                        .contentType(file.getContentType())
                        .storedPath(storedFile.getAbsolutePath())
                        .build();

                uploadedFiles.add(dto);
                log.info("Uploaded file: {} (ID: {})", file.getOriginalFilename(), fileId);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "uploadedFiles", uploadedFiles,
                    "count", uploadedFiles.size()));

        } catch (Exception e) {
            log.error("Error uploading files", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    public File getUploadedFile(String fileId) {
        return uploadedFilesCache.get(fileId);
    }

    public void clearUploadedFiles(List<String> fileIds) {
        fileIds.forEach(id -> {
            File file = uploadedFilesCache.remove(id);
            if (file != null) {
                fileStorageService.deleteFile(file);
            }
        });
    }

    private String getFileExtension(String filename) {
        if (filename == null)
            return "";
        int lastDot = filename.lastIndexOf('.');
        return (lastDot > 0) ? filename.substring(lastDot) : "";
    }
}
