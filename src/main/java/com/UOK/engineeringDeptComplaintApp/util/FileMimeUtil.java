package com.UOK.engineeringDeptComplaintApp.util;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class to check file properties, necessary for Thymeleaf conditional rendering.
 * Used by the Vice Chancellor's detail view to correctly display images vs. documents.
 */
public class FileMimeUtil {

    // Define standard image extensions
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");

    /**
     * Checks if a given file name corresponds to a standard image format.
     * @param fileName The name of the file.
     * @return true if it is an image, false otherwise.
     */
    public static boolean isImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        String lowerCaseFileName = fileName.toLowerCase();
        // Check if the file name ends with any of the defined image extensions
        return IMAGE_EXTENSIONS.stream().anyMatch(lowerCaseFileName::endsWith);
    }
}
