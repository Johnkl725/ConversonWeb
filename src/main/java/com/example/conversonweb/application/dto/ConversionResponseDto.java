package com.example.conversonweb.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for conversion response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionResponseDto {
    private String jobId;
    private String status;
    private int totalFiles;
    private int successCount;
    private int failureCount;
    private int progress;
    private List<String> convertedFiles;
    private List<String> errors;
    private String message;
}
