package com.ecaservice.test.common.service;

import com.ecaservice.test.common.exception.ConfigException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Service to read test data from resources.
 *
 * @param <T> - tests data type
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTestDataLoader<T> implements ConfigService<T> {

    private static final String TEST_DATA_DIRECTORY_IS_EMPTY = "Test data directory is empty.";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Getter
    private final Class<T> testDataClass;
    @Getter
    private final List<T> testDataModels = newArrayList();

    /**
     * Reads test data from resources.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void readTestData() throws IOException {
        log.info("Starting to read test data [{}] from configs", testDataClass.getSimpleName());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(getTestDataPath());
        if (resources.length == 0) {
            log.error(TEST_DATA_DIRECTORY_IS_EMPTY);
            throw new ConfigException(TEST_DATA_DIRECTORY_IS_EMPTY);
        } else {
            for (Resource resource : resources) {
                try {
                    @Cleanup InputStream inputStream = resource.getInputStream();
                    var testDataModel = OBJECT_MAPPER.readValue(inputStream, testDataClass);
                    testDataModels.add(testDataModel);
                } catch (IOException ex) {
                    log.error("There was an error while parsing test data json file [{}]: {}",
                            resource.getFilename(), ex.getMessage());
                    throw new ConfigException(
                            String.format("There was an error while parsing test data json file '%s': %s",
                                    resource.getFilename(), ex.getMessage()));
                }
            }
            log.info("All test data [{}] has been read from configs", testDataClass.getSimpleName());
        }
    }

    @Override
    public int count() {
        return testDataModels.size();
    }

    @Override
    public T getConfig(int index) {
        return testDataModels.get(index);
    }

    /**
     * Gets test data directory path.
     *
     * @return test data directory path
     */
    protected abstract String getTestDataPath();
}
