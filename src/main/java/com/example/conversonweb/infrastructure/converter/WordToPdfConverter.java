package com.example.conversonweb.infrastructure.converter;

import com.example.conversonweb.domain.exception.ConversionException;
import com.example.conversonweb.domain.service.FileConverter;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Converter for Word documents to PDF using docx4j
 * Reused from desktop application
 */
@Slf4j
public class WordToPdfConverter implements FileConverter {
    private static final String[] SUPPORTED_EXTENSIONS = { ".doc", ".docx" };

    @Override
    public void convertToPdf(File sourceFile, File outputFile) throws ConversionException {
        log.info("Converting Word document {} to PDF", sourceFile.getName());

        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(sourceFile);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                Docx4J.toPDF(wordMLPackage, fos);
            }

            log.info("Successfully converted {} to PDF", sourceFile.getName());

        } catch (Exception e) {
            throw new ConversionException("Error converting Word to PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }

        String fileName = file.getName().toLowerCase();
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getConverterName() {
        return "Word to PDF Converter";
    }
}
