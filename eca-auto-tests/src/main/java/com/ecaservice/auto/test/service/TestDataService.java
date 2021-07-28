package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.model.ExperimentTestDataModel;
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

    private final ObjectMapper objectMapper;

    @Getter
    private final List<ExperimentTestDataModel> experimentTestDataModels = newArrayList();

    /**
     * Reads experiments test data from resources.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void readExperimentsTestData() throws IOException {
        log.info("Starting to read experiments test data from configs");
        var resolver = new PathMatchingResourcePatternResolver();
        var classifiersOptionsFiles = resolver.getResources("");
        if (classifiersOptionsFiles.length == 0) {
            log.error(TEST_DATA_DIRECTORY_IS_EMPTY);
            //throw new ConfigException(TEST_DATA_DIRECTORY_IS_EMPTY);
        } else {
            for (Resource resource : classifiersOptionsFiles) {
                try {
                    @Cleanup InputStream inputStream = resource.getInputStream();
                   var testDataModel = objectMapper.readValue(inputStream, ExperimentTestDataModel.class);
                    experimentTestDataModels.add(testDataModel);
                } catch (IOException ex) {
                    log.error("There was an error while parsing test data json file [{}]: {}",
                            resource.getFilename(), ex.getMessage());
                  //  throw new ConfigException(
                  //          String.format("There was an error while parsing test data json file '%s': %s",
                  //                  classifierOptionsFile.getFilename(), ex.getMessage()));
                }
            }
            log.info("All test data has been read from configs");
        }
    }
}
