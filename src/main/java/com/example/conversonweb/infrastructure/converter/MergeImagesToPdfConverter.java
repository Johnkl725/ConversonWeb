package com.example.conversonweb.infrastructure.converter;

import com.example.conversonweb.domain.exception.ConversionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Converter for merging multiple images into a single PDF
 * Uses parallel processing for optimal performance
 */
@Slf4j
public class MergeImagesToPdfConverter {

    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * Merges multiple images into a single PDF file
     * Images are processed in parallel for maximum speed
     */
    public void convertMultipleToPdf(List<File> sourceFiles, File outputFile) throws ConversionException {
        log.info("Merging {} images into single PDF using {} threads", sourceFiles.size(), THREAD_POOL_SIZE);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<ImagePage>> futures = new ArrayList<>();

        try (PDDocument document = new PDDocument()) {

            // Load all images in parallel
            for (int i = 0; i < sourceFiles.size(); i++) {
                final File imageFile = sourceFiles.get(i);
                final int index = i;

                futures.add(executor.submit(() -> loadImage(imageFile, index)));
            }

            // Wait for all images to load and add them to PDF in order
            List<ImagePage> imagePages = new ArrayList<>();
            for (Future<ImagePage> future : futures) {
                try {
                    imagePages.add(future.get(60, TimeUnit.SECONDS));
                } catch (Exception e) {
                    throw new ConversionException("Error loading image: " + e.getMessage(), e);
                }
            }

            // Sort by original index to maintain order
            imagePages.sort((a, b) -> Integer.compare(a.index, b.index));

            // Add pages to document
            for (ImagePage imagePage : imagePages) {
                addImageToDocument(document, imagePage.file, imagePage.image);
            }

            // Save merged PDF
            document.save(outputFile);
            log.info("Successfully merged {} images into {}", sourceFiles.size(), outputFile.getName());

        } catch (IOException e) {
            throw new ConversionException("Error creating merged PDF: " + e.getMessage(), e);
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private ImagePage loadImage(File imageFile, int index) throws IOException {
        log.debug("Loading image: {}", imageFile.getName());
        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) {
            throw new IOException("Unable to read image: " + imageFile.getName());
        }
        return new ImagePage(index, imageFile, image);
    }

    private void addImageToDocument(PDDocument document, File imageFile, BufferedImage bufferedImage)
            throws IOException {

        PDImageXObject pdImage = PDImageXObject.createFromFile(imageFile.getAbsolutePath(), document);

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
    }

    // Helper class to maintain order
    private static class ImagePage {
        final int index;
        final File file;
        final BufferedImage image;

        ImagePage(int index, File file, BufferedImage image) {
            this.index = index;
            this.file = file;
            this.image = image;
        }
    }
}
