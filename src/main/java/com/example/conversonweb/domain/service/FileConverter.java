package com.example.conversonweb.domain.service;

import com.example.conversonweb.domain.exception.ConversionException;

import java.io.File;

/**
 * Interface for file conversion services
 * Implements Strategy Pattern - different implementations for different file
 * types
 */
public interface FileConverter {
    /**
     * Converts a source file to PDF
     * 
     * @param sourceFile Source file to convert
     * @param outputFile Output PDF file
     * @throws ConversionException if conversion fails
     */
    void convertToPdf(File sourceFile, File outputFile) throws ConversionException;

    /**
     * Checks if this converter supports the given file
     * 
     * @param file File to check
     * @return true if supported, false otherwise
     */
    boolean supports(File file);

    /**
     * Gets the converter name for logging
     * 
     * @return Converter name
     */
    String getConverterName();
}
