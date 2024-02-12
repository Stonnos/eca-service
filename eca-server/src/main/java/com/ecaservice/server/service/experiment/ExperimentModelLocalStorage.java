package com.ecaservice.server.service.experiment;

import com.ecaservice.server.config.ExperimentConfig;
import eca.core.ModelSerializationHelper;
import eca.dataminer.AbstractExperiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.ecaservice.server.util.ZipUtils.createZipArchive;

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
     * Saves experiment model to local file storage in zip format.
     *
     * @param requestId  - experiment request id
     * @param experiment - experiment model
     * @throws IOException in case of I/O errors
     */
    public void saveModelAsZip(String requestId, AbstractExperiment<?> experiment) throws IOException {
        log.info("Starting to save experiment [{}] model to local storage", requestId);
        StopWatch stopWatch = new StopWatch();

        String modelFilePath =
                String.format("%s/experiment-%s.model", experimentConfig.getExperimentLocalStoragePath(), requestId);
        File modelFile = new File(modelFilePath);
        log.info("Starting to save experiment [{}] model to file [{}]", requestId, modelFilePath);
        stopWatch.start("saveModel");
        ModelSerializationHelper.serialize(modelFile, experiment);
        stopWatch.stop();
        log.info("Experiment [{}] model has been saved to file [{}]. Total time {} ms.", requestId, modelFilePath,
                stopWatch.getLastTaskInfo().getTimeMillis());

        String modelZipFilePath =
                String.format("%s/%s.zip", experimentConfig.getExperimentLocalStoragePath(), requestId);
        log.info("Starting to save experiment [{}] model to zip file [{}]", requestId, modelZipFilePath);
        stopWatch.start("saveZipModel");
        createZipArchive(modelFilePath, modelZipFilePath);
        stopWatch.stop();
        log.info("Experiment [{}] model has been saved to zip file [{}]. Total time {} ms.", requestId,
                modelZipFilePath, stopWatch.getLastTaskInfo().getTimeMillis());

        //Deletes original model file from disk
        deleteFile(modelFile);
        log.info("Experiment [{}] model has been saved to local storage", requestId);
    }

    /**
     * Gets experiment model from local file storage.
     *
     * @param requestId - experiment request id
     * @return experiment model input stream
     */
    public InputStream getModelInputStream(String requestId) {
        try {
            log.info("Starting to get experiment [{}] model from local file storage", requestId);
            File modelZipFile = getModelZipFile(requestId);
            return new FileInputStream(modelZipFile);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Deletes experiment model from local storage.
     *
     * @param requestId - experiment request id
     */
    public void deleteModel(String requestId) {
        File file = getModelZipFile(requestId);
        deleteFile(file);
    }

    private File getModelZipFile(String requestId) {
        return new File(String.format("%s/%s.zip", experimentConfig.getExperimentLocalStoragePath(), requestId));
    }

    private void deleteFile(File file) {
        log.info("Starting to delete experiment file [{}] from local file storage", file.getAbsolutePath());
        FileUtils.deleteQuietly(file);
        log.info("Experiment model file [{}] has been deleted from local file storage", file.getAbsolutePath());
    }
}
