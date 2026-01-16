package com.example.conversonweb.application.dto;

import com.example.conversonweb.domain.model.ConversionType;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO for conversion request
 */
@Data
public class ConversionRequestDto {

    @NotEmpty(message = "File IDs cannot be empty")
    private List<String> fileIds;

    @NotNull(message = "Conversion type is required")
    private ConversionType conversionType;
}
