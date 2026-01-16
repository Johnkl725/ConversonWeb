package com.example.conversonweb.infrastructure.converter;

import com.example.conversonweb.domain.exception.ConversionException;
import com.example.conversonweb.domain.service.FileConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Converter for images to PDF using Apache PDFBox
 * Reused from desktop application
 */
@Slf4j
public class ImageToPdfConverter implements FileConverter {
    private static final String[] SUPPORTED_EXTENSIONS = { ".jpg", ".jpeg", ".png", ".bmp", ".gif", ".tiff", ".tif" };

    @Override
    public void convertToPdf(File sourceFile, File outputFile) throws ConversionException {
        log.info("Converting image {} to PDF", sourceFile.getName());

        try (PDDocument document = new PDDocument()) {
            BufferedImage image = ImageIO.read(sourceFile);

            if (image == null) {
                throw new ConversionException("Unable to read image file: " + sourceFile.getName());
            }

            PDImageXObject pdImage = PDImageXObject.createFromFile(sourceFile.getAbsolutePath(), document);

            float width = pdImage.getWidth();
            float height = pdImage.getHeight();

            // Scale to A4 if too large
            PDRectangle pageSize = PDRectangle.A4;
            if (width > pageSize.getWidth() || height > pageSize.getHeight()) {
                float scale = Math.min(pageSize.getWidth() / width, pageSize.getHeight() / height);
                width *= scale;
                height *= scale;
            }

            PDPage page = new PDPage(new PDRectangle(width, height));
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, 0, 0, width, height);
            }

            document.save(outputFile);
            log.info("Successfully converted {} to PDF", sourceFile.getName());

        } catch (Exception e) {
            throw new ConversionException("Error converting image to PDF: " + e.getMessage(), e);
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
        return "Image to PDF Converter";
    }
}
