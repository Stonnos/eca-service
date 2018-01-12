package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.mapping.ClassifierOptionsMapper;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import weka.classifiers.AbstractClassifier;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for saving individual classifiers input options into database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentConfigurationService {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private final ExperimentConfig experimentConfig;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    private final List<ClassifierOptionsMapper> classifierOptionsMappers;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentConfig                         {@link ExperimentConfig} bean
     * @param classifierOptionsDatabaseModelRepository {@link ClassifierOptionsDatabaseModelRepository} bean
     * @param classifierOptionsMappers                 {@link ClassifierOptionsMapper} bean
     */
    @Autowired
    public ExperimentConfigurationService(ExperimentConfig experimentConfig,
                                          ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository,
                                          List<ClassifierOptionsMapper> classifierOptionsMappers) {
        this.experimentConfig = experimentConfig;
        this.classifierOptionsDatabaseModelRepository = classifierOptionsDatabaseModelRepository;
        this.classifierOptionsMappers = classifierOptionsMappers;
    }

    /**
     * Saves individual classifiers input options into database.
     */
    public void saveClassifiersOptions() {
        if (StringUtils.isEmpty(experimentConfig.getIndividualClassifiersStoragePath())) {
            logAndThrowError("Classifiers input options directory doesn't specified.");
        }
        URL modelsUrl = getClass().getClassLoader().getResource(experimentConfig.getIndividualClassifiersStoragePath());
        if (!Optional.ofNullable(modelsUrl).map(URL::getPath).isPresent()) {
            logAndThrowError("Models directory not found.");
        }
        File classifiersOptionsDir = new File(modelsUrl.getPath());
        File[] modelFiles = classifiersOptionsDir.listFiles();
        if (modelFiles == null || modelFiles.length == 0) {
            logAndThrowError("Classifiers input options directory is empty.");
        } else {
            int version = getLatestVersion();
            List<ClassifierOptionsDatabaseModel> latestOptions =
                    classifierOptionsDatabaseModelRepository.findAllByVersion(version);
            List<ClassifierOptionsDatabaseModel> newOptions = createClassifiersOptions(modelFiles, version + 1);
            if (CollectionUtils.isEmpty(latestOptions) || latestOptions.size() != newOptions.size() ||
                    !latestOptions.equals(newOptions)) {
                log.info("Saving new classifiers input options with version {}.", version);
                classifierOptionsDatabaseModelRepository.save(newOptions);
            }
        }
    }

    /**
     * Reads classifiers configurations from database.
     *
     * @return {@link AbstractClassifier} list
     */
    public List<AbstractClassifier> findClassifiers() {
        log.info("Starting to read classifiers input options config from database");
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAllByVersion(getLatestVersion());
        if (CollectionUtils.isEmpty(classifierOptionsDatabaseModels)) {
            logAndThrowError("Classifiers options config hasn't been found.");
        }
        log.trace("{} classifiers configs has been found.", classifierOptionsDatabaseModels.size());
        List<AbstractClassifier> classifierList = new ArrayList<>(classifierOptionsDatabaseModels.size());
        try {
            for (ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel : classifierOptionsDatabaseModels) {
                ClassifierOptions classifierOptions =
                        objectMapper.readValue(classifierOptionsDatabaseModel.getConfig(), ClassifierOptions.class);
                for (ClassifierOptionsMapper optionsMapper : classifierOptionsMappers) {
                    if (optionsMapper.canMap(classifierOptions)) {
                        classifierList.add(optionsMapper.map(classifierOptions));
                    }
                }
            }
        } catch (Exception ex) {
            logAndThrowError(
                    String.format("There was an error while read classifiers input options config from database: %s",
                            ex.getMessage()));
        }
        log.info("{} classifiers input options config has been successfully read from database.",
                classifierList.size());
        return classifierList;
    }

    private int getLatestVersion() {
        Integer latestVersion = classifierOptionsDatabaseModelRepository.findLatestVersion();
        return latestVersion == null ? 0 : latestVersion;
    }

    private List<ClassifierOptionsDatabaseModel> createClassifiersOptions(File[] modelFiles, int version) {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels = new ArrayList<>(modelFiles.length);
        for (File modelFile : modelFiles) {
            classifierOptionsDatabaseModels.add(createClassifierOptions(modelFile, version));
        }
        return classifierOptionsDatabaseModels;
    }

    private ClassifierOptionsDatabaseModel createClassifierOptions(File modelFile, int version) {
        try {
            ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
            classifierOptionsDatabaseModel.setVersion(version);
            ClassifierOptions classifierOptions = objectMapper.readValue(modelFile, ClassifierOptions.class);
            classifierOptionsDatabaseModel.setConfig(objectMapper.writeValueAsString(classifierOptions));
            classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
            return classifierOptionsDatabaseModel;
        } catch (IOException ex) {
            log.error("There was an error while parsing json file '{}': {}", modelFile.getAbsolutePath(),
                    ex.getMessage());
            throw new ExperimentException(ex.getMessage());
        }
    }

    private void logAndThrowError(String message) {
        log.error(message);
        throw new ExperimentException(message);
    }
}
