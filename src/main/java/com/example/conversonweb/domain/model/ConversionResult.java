package com.example.conversonweb.domain.model;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Value object representing the result of a conversion operation.
 * Encapsulates success/failure information and details about converted files.
 */
@Data
@Builder
public class ConversionResult {
    private final int successCount;
    private final int failureCount;

    @Builder.Default
    private final List<File> convertedFiles = new ArrayList<>();

    @Builder.Default
    private final List<String> errors = new ArrayList<>();

    private final boolean success;

    public boolean isCompleteSuccess() {
        return failureCount == 0 && successCount > 0;
    }

    public boolean isPartialSuccess() {
        return successCount > 0 && failureCount > 0;
    }

    public boolean isCompleteFailure() {
        return successCount == 0 && failureCount > 0;
    }

    public int getTotalProcessed() {
        return successCount + failureCount;
    }

    public String getSummary() {
        if (isCompleteSuccess()) {
            return String.format("¡Todos los %d archivos se convirtieron exitosamente!", successCount);
        } else if (isPartialSuccess()) {
            return String.format("Se convirtieron %d de %d archivos. %d fallaron.",
                    successCount, getTotalProcessed(), failureCount);
        } else if (isCompleteFailure()) {
            return String.format("Todos los %d archivos fallaron al convertirse.", failureCount);
        } else {
            return "No se procesaron archivos.";
        }
    }
}
