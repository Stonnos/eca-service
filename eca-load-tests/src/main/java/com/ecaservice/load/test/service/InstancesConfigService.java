package com.ecaservice.load.test.service;

import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.exception.ConfigException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Instances config service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesConfigService implements ConfigService<Resource> {

    private static final String TRAINING_DATA_INPUT_OPTIONS_DIRECTORY_IS_EMPTY =
            "Training data input options directory is empty.";

    private final EcaLoadTestsConfig ecaLoadTestsConfig;

    private Resource[] samplesFiles;

    /**
     * Reads classifiers options from resources.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void readTrainingDataResources() throws IOException {
        log.info("Starting to read training data files info from configs");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        samplesFiles = resolver.getResources(ecaLoadTestsConfig.getTrainingDataStoragePath());
        if (samplesFiles.length == 0) {
            log.error(TRAINING_DATA_INPUT_OPTIONS_DIRECTORY_IS_EMPTY);
            throw new ConfigException(TRAINING_DATA_INPUT_OPTIONS_DIRECTORY_IS_EMPTY);
        } else {
            log.info("All training data files info has been read from configs");
        }
    }

    @Override
    public int size() {
        return samplesFiles.length;
    }

    @Override
    public Resource getConfig(int index) {
        return samplesFiles[index];
    }
}
