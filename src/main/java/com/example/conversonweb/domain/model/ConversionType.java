package com.example.conversonweb.domain.model;

/**
 * Enumeration representing the types of file conversions supported by the
 * application.
 * This follows the Open/Closed principle - new conversion types can be added
 * without modifying existing code.
 */
public enum ConversionType {
    /**
     * Converts Microsoft Word documents (.doc, .docx) to PDF format - one PDF per
     * file
     */
    WORD_TO_PDF("Word a PDF", new String[] { ".doc", ".docx" }, false),

    /**
     * Converts image files (JPG, PNG, BMP, GIF, TIFF) to PDF format - one PDF per
     * file
     */
    IMAGE_TO_PDF("Imagen a PDF", new String[] { ".jpg", ".jpeg", ".png", ".bmp", ".gif", ".tiff", ".tif" }, false),

    /**
     * Merges multiple images into a single PDF document
     */
    MERGE_IMAGES_TO_PDF("Combinar Imágenes en un PDF",
            new String[] { ".jpg", ".jpeg", ".png", ".bmp", ".gif", ".tiff", ".tif" }, true),

    /**
     * Merges multiple Word documents into a single PDF document
     */
    MERGE_WORDS_TO_PDF("Combinar Words en un PDF", new String[] { ".doc", ".docx" }, true);

    private final String displayName;
    private final String[] supportedExtensions;
    private final boolean isMergeOperation;

    ConversionType(String displayName, String[] supportedExtensions, boolean isMergeOperation) {
        this.displayName = displayName;
        this.supportedExtensions = supportedExtensions;
        this.isMergeOperation = isMergeOperation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getSupportedExtensions() {
        return supportedExtensions;
    }

    public boolean isMergeOperation() {
        return isMergeOperation;
    }

    public boolean supportsExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }

        String normalizedExt = extension.toLowerCase();
        if (!normalizedExt.startsWith(".")) {
            normalizedExt = "." + normalizedExt;
        }

        for (String supported : supportedExtensions) {
            if (supported.equalsIgnoreCase(normalizedExt)) {
                return true;
            }
        }
        return false;
    }
}
