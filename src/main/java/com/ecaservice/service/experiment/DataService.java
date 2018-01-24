package com.ecaservice.service.experiment;


import eca.converters.ModelConverter;
import eca.converters.model.ExperimentHistory;
import eca.data.file.FileDataLoader;
import eca.data.file.FileDataSaver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.File;

/**
 * Data service interface.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class DataService {

    private final FileDataSaver dataSaver;
    private final FileDataLoader dataLoader;

    /**
     * Constructor with dependency spring injection.
     *
     * @param dataSaver  {@link FileDataSaver} bean
     * @param dataLoader {@link FileDataLoader} bean
     */
    @Autowired
    public DataService(FileDataSaver dataSaver, FileDataLoader dataLoader) {
        this.dataSaver = dataSaver;
        this.dataLoader = dataLoader;
    }

    /**
     * Saves data to file.
     *
     * @param file file object
     * @param data {@link Instances} object
     * @throws Exception
     */
    public void save(File file, Instances data) throws Exception {
        log.info("Starting to save {} data into file {}.", data.relationName(), file.getAbsolutePath());
        dataSaver.saveData(file, data);
        log.info("{} data has been successfully saved to file {}.", data.relationName(), file.getAbsolutePath());
    }

    /**
     * Loads data from file.
     *
     * @param file file object
     * @return {@link Instances} object
     * @throws Exception
     */
    public Instances load(File file) throws Exception {
        log.info("Starting to load data from file {}", file.getAbsolutePath());
        dataLoader.setFile(file);
        Instances data = dataLoader.loadInstances();
        log.info("{} data has been successfully loaded from file {}", data.relationName(), file.getAbsolutePath());
        return data;
    }

    /**
     * Saves experiment history to file.
     *
     * @param file              file object
     * @param experimentHistory {@link ExperimentHistory} object
     * @throws Exception
     */
    public void save(File file, ExperimentHistory experimentHistory) throws Exception {
        log.info("Starting to save experiment history to file {}", file.getAbsolutePath());
        ModelConverter.saveModel(file, experimentHistory);
        log.info("Experiment history has been successfully saved to file {}", file.getAbsolutePath());
    }

    /**
     * Deletes file from disk.
     *
     * @param file file object
     */
    public void delete(File file) {
        boolean deleted = FileUtils.deleteQuietly(file);
        if (deleted) {
            log.info("File {} has been deleted from disk.", file.getAbsolutePath());
        } else {
            log.warn("File {} hasn't been deleted from disk.", file.getAbsolutePath());
        }
    }
}
