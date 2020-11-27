package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.exception.ConfigException;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Service to read test requests from resources.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestDataService {

    private static final String TEST_DATA_DIRECTORY_IS_EMPTY = "Test data directory is empty.";

    private final ExternalApiTestsConfig externalApiTestsConfig;
    private final ObjectMapper objectMapper;

    @Getter
    private final List<TestDataModel> testDataModels = newArrayList();

    /**
     * Reads test data from resources.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void readTestData() throws IOException {
        log.info("Starting to read test data from configs");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] classifiersOptionsFiles = resolver.getResources(externalApiTestsConfig.getTestDataPath());
        if (classifiersOptionsFiles.length == 0) {
            log.error(TEST_DATA_DIRECTORY_IS_EMPTY);
            throw new ConfigException(TEST_DATA_DIRECTORY_IS_EMPTY);
        } else {
            for (Resource classifierOptionsFile : classifiersOptionsFiles) {
                try {
                    @Cleanup InputStream inputStream = classifierOptionsFile.getInputStream();
                    TestDataModel testDataModel = objectMapper.readValue(inputStream, TestDataModel.class);
                    testDataModels.add(testDataModel);
                } catch (IOException ex) {
                    log.error("There was an error while parsing test data json file [{}]: {}",
                            classifierOptionsFile.getFilename(), ex.getMessage());
                    throw new ConfigException(
                            String.format("There was an error while parsing test data json file '%s': %s",
                                    classifierOptionsFile.getFilename(), ex.getMessage()));
                }
            }
            log.info("All test data has been read from configs");
        }
    }
}
