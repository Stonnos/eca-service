package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ClassifierOptionsException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.util.ClassifierOptionsHelper.createClassifierOptionsDatabaseModel;

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
    public static final String CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_NOT_SPECIFIED =
            "Classifiers input options directory isn't specified.";
    public static final String CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_EMPTY =
            "Classifiers input options directory is empty.";
    private static ObjectMapper objectMapper = new ObjectMapper();

    private final ExperimentConfig experimentConfig;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;

    /**
     * Saves individual classifiers input options into database.
     */
    @PostConstruct
    public void saveClassifiersOptions() {
        if (StringUtils.isEmpty(experimentConfig.getIndividualClassifiersStoragePath())) {
            log.error(CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_NOT_SPECIFIED);
            throw new ClassifierOptionsException(CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_NOT_SPECIFIED);
        }
        File classifiersOptionsDir = new File(getClass().getClassLoader().getResource(
                experimentConfig.getIndividualClassifiersStoragePath()).getFile());
        Collection<File> modelFiles = FileUtils.listFiles(classifiersOptionsDir, null, true);
        if (CollectionUtils.isEmpty(modelFiles)) {
            log.error(CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_EMPTY);
            throw new ClassifierOptionsException(CLASSIFIERS_INPUT_OPTIONS_DIRECTORY_IS_EMPTY);
        } else {
            log.info("Starting to save individual classifiers options into database");
            ClassifiersConfiguration classifiersConfiguration = getOrSaveBuildInClassifiersConfiguration();
            List<ClassifierOptionsDatabaseModel> latestOptions =
                    classifierOptionsDatabaseModelRepository.findAllByConfiguration(classifiersConfiguration);
            List<ClassifierOptionsDatabaseModel> newOptions =
                    createClassifiersOptions(modelFiles, classifiersConfiguration);
            updateSystemClassifiersConfiguration(newOptions, latestOptions);
        }
    }

    private void updateSystemClassifiersConfiguration(List<ClassifierOptionsDatabaseModel> newOptions,
                                                      List<ClassifierOptionsDatabaseModel> latestOptions) {
        if (CollectionUtils.isEmpty(latestOptions) || latestOptions.size() != newOptions.size() ||
                !newOptions.containsAll(latestOptions)) {
            log.info("Saving new classifiers input options for system configuration.");
            List<ClassifierOptionsDatabaseModel> oldOptionsToDelete =
                    latestOptions.stream().filter(options -> !newOptions.contains(options)).collect(
                            Collectors.toList());
            List<ClassifierOptionsDatabaseModel> newOptionsToSave =
                    newOptions.stream().filter(options -> !latestOptions.contains(options)).collect(
                            Collectors.toList());
            if (!CollectionUtils.isEmpty(oldOptionsToDelete)) {
                classifierOptionsDatabaseModelRepository.deleteAll(oldOptionsToDelete);
            }
            if (!CollectionUtils.isEmpty(newOptionsToSave)) {
                classifierOptionsDatabaseModelRepository.saveAll(newOptions);
            }
        }
    }

    private ClassifiersConfiguration getOrSaveBuildInClassifiersConfiguration() {
        ClassifiersConfiguration classifiersConfiguration =
                classifiersConfigurationRepository.findFirstByBuildInIsTrue();
        if (classifiersConfiguration == null) {
            classifiersConfiguration = new ClassifiersConfiguration();
            classifiersConfiguration.setName(DEFAULT_CONFIGURATION_NAME);
            classifiersConfiguration.setBuildIn(true);
            classifiersConfiguration.setActive(true);
            classifiersConfiguration.setCreated(LocalDateTime.now());
            return classifiersConfigurationRepository.save(classifiersConfiguration);
        }
        return classifiersConfiguration;
    }

    private List<ClassifierOptionsDatabaseModel> createClassifiersOptions(Collection<File> modelFiles,
                                                                          ClassifiersConfiguration classifiersConfiguration) {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels = new ArrayList<>(modelFiles.size());
        for (File modelFile : modelFiles) {
            try {
                classifierOptionsDatabaseModels.add(
                        createClassifierOptionsDatabaseModel(objectMapper.readValue(modelFile, ClassifierOptions.class),
                                classifiersConfiguration));
            } catch (IOException ex) {
                log.error("There was an error while parsing json file [{}]: {}", modelFile.getAbsolutePath(),
                        ex.getMessage());
                throw new ClassifierOptionsException(
                        String.format("There was an error while parsing json file '%s': %s",
                                modelFile.getAbsolutePath(), ex.getMessage()));
            }
        }
        return classifierOptionsDatabaseModels;
    }

}
