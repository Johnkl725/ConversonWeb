package com.example.conversonweb.infrastructure.converter;

import com.example.conversonweb.domain.exception.ConversionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Converter for merging multiple Word documents into a single PDF
 * Uses parallel processing for optimal performance
 */
@Slf4j
public class MergeWordsToPdfConverter {

    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * Merges multiple Word documents into a single PDF file
     * Each Word is converted to PDF in parallel, then merged
     */
    public void convertMultipleToPdf(List<File> sourceFiles, File outputFile) throws ConversionException {
        log.info("Merging {} Word documents into single PDF using {} threads",
                sourceFiles.size(), THREAD_POOL_SIZE);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<TempPdf>> futures = new ArrayList<>();
        List<File> tempPdfFiles = new ArrayList<>();

        try {
            // Convert each Word to temporary PDF in parallel
            for (int i = 0; i < sourceFiles.size(); i++) {
                final File wordFile = sourceFiles.get(i);
                final int index = i;

                futures.add(executor.submit(() -> convertWordToTempPdf(wordFile, index)));
            }

            // Wait for all conversions and collect temp PDFs in order
            List<TempPdf> tempPdfs = new ArrayList<>();
            for (Future<TempPdf> future : futures) {
                try {
                    tempPdfs.add(future.get(120, TimeUnit.SECONDS));
                } catch (Exception e) {
                    throw new ConversionException("Error converting Word to PDF: " + e.getMessage(), e);
                }
            }

            // Sort by original index to maintain order
            tempPdfs.sort((a, b) -> Integer.compare(a.index, b.index));

            // Collect temp files for cleanup
            for (TempPdf tempPdf : tempPdfs) {
                tempPdfFiles.add(tempPdf.file);
            }

            // Merge all PDFs into final output
            mergePdfs(tempPdfFiles, outputFile);

            log.info("Successfully merged {} Word documents into {}",
                    sourceFiles.size(), outputFile.getName());

        } catch (Exception e) {
            throw new ConversionException("Error creating merged PDF: " + e.getMessage(), e);
        } finally {
            // Cleanup temp files
            for (File tempFile : tempPdfFiles) {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                } catch (IOException e) {
                    log.warn("Could not delete temp file: {}", tempFile.getName());
                }
            }

            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private TempPdf convertWordToTempPdf(File wordFile, int index) throws Exception {
        log.debug("Converting Word to PDF: {}", wordFile.getName());

        // Create temp PDF file
        File tempPdf = File.createTempFile("word_to_pdf_", ".pdf");

        // Convert Word to PDF
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(wordFile);
        try (FileOutputStream fos = new FileOutputStream(tempPdf)) {
            Docx4J.toPDF(wordMLPackage, fos);
        }

        return new TempPdf(index, tempPdf);
    }

    private void mergePdfs(List<File> pdfFiles, File outputFile) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(outputFile.getAbsolutePath());

        for (File pdfFile : pdfFiles) {
            merger.addSource(pdfFile);
        }

        merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
    }

    // Helper class to maintain order
    private static class TempPdf {
        final int index;
        final File file;

        TempPdf(int index, File file) {
            this.index = index;
            this.file = file;
        }
    }
}
