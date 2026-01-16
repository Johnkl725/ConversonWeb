package com.example.conversonweb.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for uploaded file information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadDto {
    private String id;
    private String originalName;
    private long size;
    private String contentType;
    private String storedPath;
}
