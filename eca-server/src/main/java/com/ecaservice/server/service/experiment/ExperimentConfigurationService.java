package com.ecaservice.server.service.experiment;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.exception.ClassifierOptionsException;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.classifiers.ClassifierOptionsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Set;

import static com.ecaservice.server.util.ClassifierOptionsHelper.createClassifierOptionsDatabaseModel;
import static com.google.common.collect.Sets.newHashSet;

/**
 * Service for saving individual classifiers input options into database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentConfigurationService {

    private static final String DEFAULT_CONFIGURATION_NAME = "Default configuration";
    private static final String CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_NOT_SPECIFIED =
            "Classifiers input options directory isn't specified.";
    private static final String CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_EMPTY =
            "Classifiers input options directory is empty.";
    private static ObjectMapper objectMapper = new ObjectMapper();

    private final ClassifierOptionsService classifierOptionsService;
    private final ExperimentConfig experimentConfig;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;

    /**
     * Saves individual classifiers input options into database.
     */
    public void saveClassifiersOptions() throws IOException {
        if (StringUtils.isEmpty(experimentConfig.getIndividualClassifiersStoragePath())) {
            log.error(CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_NOT_SPECIFIED);
            throw new ClassifierOptionsException(CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_NOT_SPECIFIED);
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] modelFiles = resolver.getResources(experimentConfig.getIndividualClassifiersStoragePath());
        if (modelFiles.length == 0) {
            log.error(CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_EMPTY);
            throw new ClassifierOptionsException(CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_EMPTY);
        } else {
            log.info("Starting to save individual classifiers options into database for build in configuration");
            ClassifiersConfiguration classifiersConfiguration = getOrSaveBuildInClassifiersConfiguration();
            Set<ClassifierOptionsDatabaseModel> newOptions =
                    createClassifiersOptions(modelFiles, classifiersConfiguration);
            classifierOptionsService.updateBuildInClassifiersConfiguration(classifiersConfiguration, newOptions);
        }
    }

    private ClassifiersConfiguration getOrSaveBuildInClassifiersConfiguration() {
        ClassifiersConfiguration classifiersConfiguration =
                classifiersConfigurationRepository.findFirstByBuildInIsTrue();
        if (classifiersConfiguration == null) {
            classifiersConfiguration = new ClassifiersConfiguration();
            classifiersConfiguration.setConfigurationName(DEFAULT_CONFIGURATION_NAME);
            classifiersConfiguration.setBuildIn(true);
            classifiersConfiguration.setActive(true);
            classifiersConfiguration.setCreationDate(LocalDateTime.now());
            return classifiersConfigurationRepository.save(classifiersConfiguration);
        }
        return classifiersConfiguration;
    }

    private Set<ClassifierOptionsDatabaseModel> createClassifiersOptions(Resource[] modelFiles,
                                                                         ClassifiersConfiguration classifiersConfiguration) {
        Set<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels = newHashSet();
        for (Resource modelFile : modelFiles) {
            try {
                @Cleanup InputStream inputStream = modelFile.getInputStream();
                classifierOptionsDatabaseModels.add(createClassifierOptionsDatabaseModel(
                        objectMapper.readValue(inputStream, ClassifierOptions.class), classifiersConfiguration));
            } catch (IOException ex) {
                log.error("There was an error while parsing json file [{}]: {}", modelFile.getFilename(),
                        ex.getMessage());
                throw new ClassifierOptionsException(
                        String.format("There was an error while parsing json file '%s': %s", modelFile.getFilename(),
                                ex.getMessage()));
            }
        }
        return classifierOptionsDatabaseModels;
    }

}
