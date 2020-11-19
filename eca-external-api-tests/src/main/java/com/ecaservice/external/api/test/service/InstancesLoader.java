package com.ecaservice.external.api.test.service;

import eca.data.file.FileDataLoader;
import eca.data.file.resource.AbstractResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service for loading data from file.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class InstancesLoader {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Loads training data from file.
     *
     * @param location - training data location
     * @return instances object
     * @throws Exception in case of error
     */
    @Cacheable(value = "instances")
    public Instances loadInstances(String location) throws Exception {
        Resource resource = resolver.getResource(location);
        log.info("Starting to load data from file {}", resource.getFilename());
        Instances data = loadInstances(resource);
        log.info("{} data has been successfully loaded from file {}", data.relationName(), resource.getFilename());
        return data;
    }

    private Instances loadInstances(Resource resource) throws Exception {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setSource(new AbstractResource<Resource>(resource) {
            @Override
            public InputStream openInputStream() throws IOException {
                return resource.getInputStream();
            }

            @Override
            public String getFile() {
                return resource.getFilename();
            }
        });
        return dataLoader.loadInstances();
    }
}
