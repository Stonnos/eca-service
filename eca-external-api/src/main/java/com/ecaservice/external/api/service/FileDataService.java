package com.ecaservice.external.api.service;

import com.ecaservice.external.api.exception.ProcessFileException;
import eca.converters.ModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implements data service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileDataService {

    /**
     * Saves classifier model to file.
     *
     * @param classifier - classifier
     * @param file       - file object
     * @throws Exception in case of error
     */
    public void saveModel(Object classifier, File file) throws Exception {
        ModelConverter.saveModel(file, classifier);
    }

    /**
     * Copies input stream to file.
     *
     * @param inputStream - input stream
     * @param destination - destination file
     * @throws IOException in case of I/O error
     */
    public void copyToFile(InputStream inputStream, File destination) throws IOException {
        FileUtils.copyInputStreamToFile(inputStream, destination);
    }

    /**
     * Deletes file from disk.
     *
     * @param fileName - file name
     */
    public void delete(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            log.warn("Got empty file name. Mark as deleted");
        }
        File file = new File(fileName);
        if (!file.isFile()) {
            log.warn("File with name [{}] doesn't exists. Mark as deleted", file.getAbsolutePath());
        } else {
            try {
                FileUtils.forceDelete(file);
                log.info("File [{}] has been deleted from disk.", file.getAbsolutePath());
            } catch (IOException ex) {
                log.error("There was an error while deleting [{}] file from disk.", file.getAbsolutePath());
                throw new ProcessFileException(ex.getMessage());
            }
        }
    }
}
