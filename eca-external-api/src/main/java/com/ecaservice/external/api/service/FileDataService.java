package com.ecaservice.external.api.service;

import eca.converters.ModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;

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
     * Deletes file from disk.
     *
     * @param fileName - file name
     */
    public boolean delete(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return true;
        }
        File file = new File(fileName);
        if (!file.isFile()) {
            log.warn("File with name [{}] doesn't exists. Mark as deleted", file.getAbsolutePath());
            return true;
        } else {
            boolean deleted = FileUtils.deleteQuietly(file);
            if (deleted) {
                log.info("File [{}] has been deleted from disk.", file.getAbsolutePath());
            } else {
                log.warn("There was an error while deleting [{}] file from disk.", file.getAbsolutePath());
            }
            return deleted;
        }
    }
}
