package com.ecaservice.service.experiment;


import com.ecaservice.exception.experiment.ExperimentException;
import eca.converters.ModelConverter;
import eca.converters.model.ExperimentHistory;
import eca.data.file.FileDataLoader;
import eca.data.file.FileDataSaver;
import eca.data.file.resource.FileResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;

/**
 * Data service interface.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

    private final FileDataSaver dataSaver;
    private final FileDataLoader dataLoader;

    /**
     * Saves data to file.
     *
     * @param file - file object
     * @param data - training data
     * @throws Exception in case of I/O error
     */
    public void save(File file, Instances data) throws Exception {
        log.info("Starting to save {} data into file {}.", data.relationName(), file.getAbsolutePath());
        dataSaver.saveData(file, data);
        log.info("{} data has been successfully saved to file {}.", data.relationName(), file.getAbsolutePath());
    }

    /**
     * Loads data from file.
     *
     * @param file - file object
     * @return training data
     * @throws Exception in case of I/O error
     */
    public Instances load(File file) throws Exception {
        log.info("Starting to load data from file {}", file.getAbsolutePath());
        dataLoader.setSource(new FileResource(file));
        Instances data = dataLoader.loadInstances();
        log.info("{} data has been successfully loaded from file {}", data.relationName(), file.getAbsolutePath());
        return data;
    }

    /**
     * Saves experiment history to file.
     *
     * @param file              - file object
     * @param experimentHistory - experiment history
     * @throws Exception in case of I/O error
     */
    public void saveExperimentHistory(File file, ExperimentHistory experimentHistory) throws Exception {
        log.info("Starting to save experiment history to file {}", file.getAbsolutePath());
        ModelConverter.saveModel(file, experimentHistory);
        log.info("Experiment history has been successfully saved to file {}", file.getAbsolutePath());
    }

    /**
     * Loads experiment history from file.
     *
     * @param file - file object
     * @return experiment history
     * @throws Exception in case of I/O error
     */
    public ExperimentHistory loadExperimentHistory(File file) throws Exception {
        log.info("Starting to load experiment history from file {}", file.getAbsolutePath());
        ExperimentHistory experimentHistory = ModelConverter.loadModel(file, ExperimentHistory.class);
        log.info("Experiment history has been loaded from file {}", file.getAbsolutePath());
        return experimentHistory;
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
                throw new ExperimentException(ex.getMessage());
            }
        }
    }
}
