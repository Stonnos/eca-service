package com.ecaservice.server.service.experiment;

import com.ecaservice.server.config.ExperimentConfig;
import eca.core.ModelSerializationHelper;
import eca.dataminer.AbstractExperiment;
import io.micrometer.tracing.annotation.NewSpan;
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
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

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
    @NewSpan
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
                String.format("%s/experiment-%s.zip", experimentConfig.getExperimentLocalStoragePath(), requestId);
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
     * Saves experiment data file to local file storage.
     *
     * @param fileName - file name
     * @param data     - some data
     */
    @NewSpan
    public void saveDataFile(String fileName, Serializable data) {
        try {
            String filePath = String.format("%s/%s", experimentConfig.getExperimentLocalStoragePath(), fileName);
            log.info("Starting to save experiment data file [{}] to local storage", filePath);
            StopWatch stopWatch = new StopWatch();
            File dataFile = new File(filePath);
            stopWatch.start();
            ModelSerializationHelper.serialize(dataFile, data);
            stopWatch.stop();
            log.info("Experiment data has been saved to file [{}]. Total time {} ms.", filePath,
                    stopWatch.getTotalTime(TimeUnit.MILLISECONDS));
            log.info("Experiment data file [{}] has been saved to local storage", filePath);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Gets experiment details data from local file storage.
     *
     * @param fileName - file name
     * @return experiment data file input stream
     */
    public InputStream getDataFileInputStream(String fileName) {
        try {
            log.info("Starting to get experiment data [{}] from local file storage", fileName);
            File file = getDataFile(fileName);
            return new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Deletes experiment model from local storage.
     *
     * @param fileName - experiment request id
     */
    @NewSpan
    public void deleteDataFile(String fileName) {
        File file = getDataFile(fileName);
        deleteFile(file);
    }

    private File getDataFile(String fileName) {
        return new File(String.format("%s/%s", experimentConfig.getExperimentLocalStoragePath(), fileName));
    }

    private void deleteFile(File file) {
        log.info("Starting to delete experiment file [{}] from local file storage", file.getAbsolutePath());
        FileUtils.deleteQuietly(file);
        log.info("Experiment model file [{}] has been deleted from local file storage", file.getAbsolutePath());
    }
}
