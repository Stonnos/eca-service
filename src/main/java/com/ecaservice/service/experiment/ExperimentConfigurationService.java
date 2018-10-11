package com.ecaservice.service.experiment;

import com.ecaservice.config.EcaServiceParam;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ecaservice.util.ExperimentLogUtils.error;

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

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentConfig                         - experiment config bean
     * @param classifierOptionsDatabaseModelRepository - classifier options database model repository bean
     */
    @Inject
    public ExperimentConfigurationService(ExperimentConfig experimentConfig,
                                          ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository) {
        this.experimentConfig = experimentConfig;
        this.classifierOptionsDatabaseModelRepository = classifierOptionsDatabaseModelRepository;
    }

    /**
     * Saves individual classifiers input options into database.
     */
    public void saveClassifiersOptions() {
        if (StringUtils.isEmpty(experimentConfig.getIndividualClassifiersStoragePath())) {
            error("Classifiers input options directory doesn't specified.", log);
        }
        File classifiersOptionsDir = new File(getClass().getClassLoader().getResource(
                experimentConfig.getIndividualClassifiersStoragePath()).getFile());
        Collection<File> modelFiles = FileUtils.listFiles(classifiersOptionsDir, null, true);
        if (CollectionUtils.isEmpty(modelFiles)) {
            error("Classifiers input options directory is empty.", log);
        } else {
            int version = classifierOptionsDatabaseModelRepository.findLatestVersion();
            List<ClassifierOptionsDatabaseModel> latestOptions =
                    classifierOptionsDatabaseModelRepository.findAllByVersion(version);
            List<ClassifierOptionsDatabaseModel> newOptions = createClassifiersOptions(modelFiles, ++version);
            if (CollectionUtils.isEmpty(latestOptions) || latestOptions.size() != newOptions.size() ||
                    !latestOptions.equals(newOptions)) {
                log.info("Saving new classifiers input options with version {}.", version);
                classifierOptionsDatabaseModelRepository.saveAll(newOptions);
            }
        }
    }

    /**
     * Saves classifier input options config into database.
     *
     * @param classifierOptions {@link ClassifierOptions} object
     */
    public ClassifierOptionsDatabaseModel saveClassifierOptions(ClassifierOptions classifierOptions) {
        int version = classifierOptionsDatabaseModelRepository.findLatestVersion();
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                createClassifierOptions(classifierOptions, version == 0 ? version + 1 : version);
        classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
        log.info("New classifier options {} has been saved.", classifierOptions);
        return classifierOptionsDatabaseModel;
    }

    /**
     * Finds the last classifiers options configs.
     *
     * @return {@link ClassifierOptionsDatabaseModel} list
     */
    @Cacheable(EcaServiceParam.CLASSIFIERS_CACHE_NAME)
    public List<ClassifierOptionsDatabaseModel> findLastClassifiersOptions() {
        log.info("Starting to read classifiers input options configs from database");
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModelList =
                classifierOptionsDatabaseModelRepository.findAllByVersion(
                        classifierOptionsDatabaseModelRepository.findLatestVersion());
        log.info("{} classifiers input options configs has been successfully read from database.",
                classifierOptionsDatabaseModelList.size());
        return classifierOptionsDatabaseModelList;
    }

    private List<ClassifierOptionsDatabaseModel> createClassifiersOptions(Collection<File> modelFiles, int version) {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels = new ArrayList<>(modelFiles.size());
        for (File modelFile : modelFiles) {
            try {
                classifierOptionsDatabaseModels.add(
                        createClassifierOptions(objectMapper.readValue(modelFile, ClassifierOptions.class), version));
            } catch (IOException ex) {
                error(String.format("There was an error while parsing json file '%s': %s", modelFile.getAbsolutePath(),
                        ex.getMessage()), log);
            }
        }
        return classifierOptionsDatabaseModels;
    }

    private ClassifierOptionsDatabaseModel createClassifierOptions(ClassifierOptions classifierOptions, int version) {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = null;
        try {
            classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
            classifierOptionsDatabaseModel.setVersion(version);
            classifierOptionsDatabaseModel.setOptionsName(classifierOptions.getClass().getSimpleName());
            classifierOptionsDatabaseModel.setConfig(objectMapper.writeValueAsString(classifierOptions));
            classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
            return classifierOptionsDatabaseModel;
        } catch (IOException ex) {
            error(String.format("There was an error while parsing object [%s]: %s", classifierOptions, ex.getMessage()),
                    log);
        }
        return classifierOptionsDatabaseModel;
    }

}
