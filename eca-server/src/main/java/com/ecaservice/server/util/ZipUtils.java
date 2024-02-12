package com.ecaservice.server.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ZipUtils {

    /**
     * Creates zip archive with specified file.
     *
     * @param filePath - source file path
     * @param zipPath  - target zip archive path
     */
    public static void createZipArchive(String filePath, String zipPath) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(zipPath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            File fileToZip = new File(filePath);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOutputStream.putNextEntry(zipEntry);
            Files.copy(fileToZip.toPath(), zipOutputStream);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
