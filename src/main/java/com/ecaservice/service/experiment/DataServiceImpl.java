package com.ecaservice.service.experiment;

import eca.converters.ModelConverter;
import eca.converters.model.ExperimentHistory;
import eca.data.DataLoader;
import eca.data.DataSaver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.File;

/**
 * Data service implementation.
 * @author Roman Batygin
 */
@Slf4j
@Service
public class DataServiceImpl implements DataService {

    private final DataSaver dataSaver;
    private final DataLoader dataLoader;

    /**
     * Constructor with dependency spring injection.
     * @param dataSaver {@link DataSaver} bean
     * @param dataLoader {@link DataLoader} bean
     */
    @Autowired
    public DataServiceImpl(DataSaver dataSaver, DataLoader dataLoader) {
        this.dataSaver = dataSaver;
        this.dataLoader = dataLoader;
    }

    @Override
    public void save(File file, Instances data) throws Exception {
        log.info("Starting to save {} data into file {}.", data.relationName(), file.getAbsolutePath());
        dataSaver.saveData(file, data);
        log.info("{} data has been successfully saved to file {}.", data.relationName(), file.getAbsolutePath());
    }

    @Override
    public Instances load(File file) throws Exception {
        log.info("Starting to load data from file {}", file.getAbsolutePath());
        Instances data = dataLoader.getDataSet(file);
        log.info("{} data has been successfully loaded from file {}", data.relationName(), file.getAbsolutePath());
        return data;
    }

    @Override
    public void save(File file, ExperimentHistory experimentHistory) throws Exception {
        log.info("Starting to save experiment history to file {}", file.getAbsolutePath());
        ModelConverter.saveModel(file, experimentHistory);
        log.info("Experiment history has been successfully saved to file {}", file.getAbsolutePath());
    }
}
