package com.ecaservice.test.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip utility class.
 *
 * @author Roman Batygin
 */
@Slf4j
@UtilityClass
public class ZipUtils {

    /**
     * Writes next entry in zip archive and flush buffer.
     *
     * @param zipOutputStream    - zip output stream
     * @param outputStreamWriter - output stream writer
     * @param fileName           - file name
     * @param content                - file content
     * @throws IOException in case of I/O error
     */
    public static void writeAndFlushNextEntry(ZipOutputStream zipOutputStream,
                                              OutputStreamWriter outputStreamWriter,
                                              String fileName,
                                              String content) throws IOException {
        log.info("Starting to write file [{}] into zip archive", fileName);
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        outputStreamWriter.write(content);
        flush(zipOutputStream, outputStreamWriter);
        log.info("File [{}] has been written into zip archive", fileName);
    }

    /**
     * Flushes streams buffers.
     *
     * @param zipOutputStream - zip output stream
     * @param writer          - output stream writer
     * @throws IOException in case of I/O error
     */
    public static void flush(ZipOutputStream zipOutputStream, OutputStreamWriter writer) throws IOException {
        writer.flush();
        zipOutputStream.flush();
        zipOutputStream.closeEntry();
    }
}
