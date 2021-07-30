package com.ecaservice.load.test.service;

import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.exception.ConfigException;
import com.ecaservice.test.common.service.TestDataProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * Instances config service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesTestDataProvider implements TestDataProvider<Resource> {

    private static final String TRAINING_DATA_INPUT_OPTIONS_DIRECTORY_IS_EMPTY =
            "Training data input options directory is empty.";

    private final EcaLoadTestsConfig ecaLoadTestsConfig;

    private List<Resource> testDataModels;

    /**
     * Reads classifiers options from resources.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void readTrainingDataResources() throws IOException {
        log.info("Starting to read training data files info from configs");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        testDataModels = List.of(resolver.getResources(ecaLoadTestsConfig.getTrainingDataStoragePath()));
        if (testDataModels.isEmpty()) {
            log.error(TRAINING_DATA_INPUT_OPTIONS_DIRECTORY_IS_EMPTY);
            throw new ConfigException(TRAINING_DATA_INPUT_OPTIONS_DIRECTORY_IS_EMPTY);
        } else {
            log.info("All training data files info has been read from configs");
        }
    }

    @Override
    public int count() {
        return testDataModels.size();
    }

    @Override
    public Resource getTestData(int index) {
        return testDataModels.get(index);
    }

    @Override
    public List<Resource> getTestDataModels() {
        return testDataModels;
    }
}
