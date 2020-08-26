package com.ecaservice.load.test.service;

import com.ecaservice.load.test.exception.ConfigException;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.FileResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.File;

/**
 * Service for loading data from file.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class InstancesLoader {

    /**
     * Loads training data from file.
     *
     * @param file - training data file
     * @return instances object
     */
    @Cacheable(value = "instances", key = "#file.name")
    public Instances loadInstances(File file) {
        try {
            log.info("Starting to load data from file {}", file.getName());
            FileDataLoader dataLoader = new FileDataLoader();
            dataLoader.setSource(new FileResource(file));
            Instances data = dataLoader.loadInstances();
            log.info("{} data has been successfully loaded from file {}", data.relationName(), file.getName());
            return data;
        } catch (Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
}
