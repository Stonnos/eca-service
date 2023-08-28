package com.ecaservice.data.loader.util;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    /**
     * Calculates md5 hash for specified file.
     *
     * @param filePath - file path on file system
     * @return file md5 hash string
     * @throws IOException in case of I/O error
     */
    public static String md5Hash(String filePath) throws IOException {
        @Cleanup var fileInputStream = new FileInputStream(filePath);
        @Cleanup var bufferedInputStream = new BufferedInputStream(fileInputStream);
        return DigestUtils.md5DigestAsHex(bufferedInputStream);
    }

    /**
     * Copies multipart file to file system.
     *
     * @param file     - multipart file
     * @param filePath - file path on file system
     * @throws IOException in case of I/O error
     */
    public static void copyToFile(MultipartFile file, String filePath) throws IOException {
        @Cleanup var inputStream = file.getInputStream();
        @Cleanup var fileOutputStream = new FileOutputStream(filePath);
        @Cleanup var bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        IOUtils.copyLarge(inputStream, bufferedOutputStream);
    }

    /**
     * Creates file input stream quietly.
     *
     * @param filePath - file path on file system
     * @return file input stream
     */
    public static FileInputStream createFileInputStreamQuietly(String filePath) {
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
