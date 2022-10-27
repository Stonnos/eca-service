package com.ecaservice.server.service.experiment;

import com.ecaservice.server.config.ExperimentConfig;
import eca.core.ModelSerializationHelper;
import eca.data.file.resource.FileResource;
import eca.dataminer.AbstractExperiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Experiment model local storage.
 *
 * @author Roman Batyfin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentModelLocalStorage {

    private final ExperimentConfig experimentConfig;

    /**
     * Saves experiment model to local storage.
     *
     * @param requestId  - experiment request id
     * @param experiment - experiment model
     * @throws IOException in case of I/O errors
     */
    public void saveIfAbsent(String requestId, AbstractExperiment<?> experiment) throws IOException {
        log.info("Starting to save experiment [{}] model to local storage", requestId);
        File file = getFile(requestId);
        if (!file.isFile()) {
            log.warn("Experiment [{}] file already exists. Skipped...", requestId);
        } else {
            ModelSerializationHelper.serialize(file, experiment);
            log.info("Experiment [{}] model has been saved to local storage", requestId);
        }
    }

    /**
     * Gets experiment model from local storage.
     *
     * @param requestId - experiment request id
     * @return experiment model
     * @throws IOException in case of I/O errors
     */
    public AbstractExperiment<?> get(String requestId) throws IOException {
        log.info("Starting to get experiment [{}] model from local storage", requestId);
        File file = getFile(requestId);
        FileResource fileResource = new FileResource(file);
        var experimentModel = ModelSerializationHelper.deserialize(fileResource, AbstractExperiment.class);
        log.info("Experiment [{}] model has been loaded from local storage", requestId);
        return experimentModel;
    }

    /**
     * Deletes experiment model from local storage.
     *
     * @param requestId - experiment request id
     */
    public void delete(String requestId) {
        log.info("Starting to delete experiment [{}] model from local storage", requestId);
        File file = getFile(requestId);
        FileUtils.deleteQuietly(file);
        log.info("Experiment [{}] model has been deleted from local storage", requestId);
    }

    private File getFile(String requestId) {
        return new File(String.format("%s/%s.model", experimentConfig.getExperimentLocalStoragePath(), requestId));
    }
}
