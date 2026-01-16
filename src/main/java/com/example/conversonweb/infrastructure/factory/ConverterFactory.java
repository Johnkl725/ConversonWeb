package com.example.conversonweb.infrastructure.factory;

import com.example.conversonweb.domain.model.ConversionType;
import com.example.conversonweb.domain.service.FileConverter;
import com.example.conversonweb.infrastructure.converter.ImageToPdfConverter;
import com.example.conversonweb.infrastructure.converter.WordToPdfConverter;
import org.springframework.stereotype.Component;

/**
 * Factory for creating appropriate FileConverter instances
 * Implements Factory Pattern
 */
@Component
public class ConverterFactory {

    public FileConverter createConverter(ConversionType conversionType) {
        if (conversionType == null) {
            throw new IllegalArgumentException("Conversion type cannot be null");
        }

        return switch (conversionType) {
            case WORD_TO_PDF, MERGE_WORDS_TO_PDF -> new WordToPdfConverter();
            case IMAGE_TO_PDF, MERGE_IMAGES_TO_PDF -> new ImageToPdfConverter();
        };
    }
}
