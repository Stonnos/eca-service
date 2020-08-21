package com.ecaservice.load.test.service;

import eca.data.file.FileDataLoader;
import eca.data.file.resource.FileResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import weka.core.Instances;

import javax.validation.constraints.NotEmpty;
import java.io.File;

/**
 * Service for loading data from file.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
public class InstancesService {

    /**
     * Loads training data from file.
     *
     * @param fileName - file name
     * @return instances object
     * @throws Exception in case of error
     */
    public Instances loadInstances(@NotEmpty String fileName) throws Exception {
        File file = new File(fileName);
        log.info("Starting to load data from file {}", file.getAbsolutePath());
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setSource(new FileResource(file));
        Instances data = dataLoader.loadInstances();
        log.info("{} data has been successfully loaded from file {}", data.relationName(), file.getAbsolutePath());
        return data;
    }
}
