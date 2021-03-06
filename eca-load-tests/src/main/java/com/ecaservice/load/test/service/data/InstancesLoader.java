package com.ecaservice.load.test.service.data;

import com.ecaservice.load.test.exception.ConfigException;
import com.ecaservice.load.test.model.ResourceReader;
import eca.data.file.FileDataLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import weka.core.Instances;

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
     * @param resource - training data resource
     * @return instances object
     */
    @Cacheable(value = "instances", key = "#resource.filename")
    public Instances loadInstances(Resource resource) {
        try {
            log.info("Starting to load data from file {}", resource.getFilename());
            FileDataLoader dataLoader = new FileDataLoader();
            dataLoader.setSource(new ResourceReader(resource));
            Instances data = dataLoader.loadInstances();
            log.info("{} data has been successfully loaded from file {}", data.relationName(), resource.getFilename());
            return data;
        } catch (Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
}
