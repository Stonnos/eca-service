package com.ecaservice.load.test.service;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.exception.ConfigException;
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
 * Classifiers options service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersOptionsService {

    private static final String CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_EMPTY =
            "Classifiers input options directory is empty.";

    private final EcaLoadTestsConfig ecaLoadTestsConfig;

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private List<ClassifierOptions> classifierOptionsList = newArrayList();

    /**
     * Reads classifiers options from resources.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void readClassifiersOptions() throws IOException {
        log.info("Starting to read classifiers options from configs");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] classifiersOptionsFiles = resolver.getResources(ecaLoadTestsConfig.getClassifiersStoragePath());
        if (classifiersOptionsFiles.length == 0) {
            log.error(CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_EMPTY);
            throw new ConfigException(CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_EMPTY);
        } else {
            for (Resource classifierOptionsFile : classifiersOptionsFiles) {
                try {
                    @Cleanup InputStream inputStream = classifierOptionsFile.getInputStream();
                    ClassifierOptions classifierOptions = objectMapper.readValue(inputStream, ClassifierOptions.class);
                    classifierOptionsList.add(classifierOptions);
                } catch (IOException ex) {
                    log.error("There was an error while parsing classifier options json file [{}]: {}",
                            classifierOptionsFile.getFilename(), ex.getMessage());
                    throw new ConfigException(
                            String.format("There was an error while parsing classifier options json file '%s': %s",
                                    classifierOptionsFile.getFilename(), ex.getMessage()));
                }
            }
            log.info("All classifiers options has been read from configs");
        }
    }
}
