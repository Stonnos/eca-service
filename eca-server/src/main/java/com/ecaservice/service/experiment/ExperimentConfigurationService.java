package com.ecaservice.service.experiment;

import com.ecaservice.config.CacheNames;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.service.PageRequestService;
import com.ecaservice.util.SortUtils;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ecaservice.util.ExperimentLogUtils.logAndThrowError;

/**
 * Service for saving individual classifiers input options into database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentConfigurationService implements PageRequestService<ClassifierOptionsDatabaseModel> {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private final CommonConfig commonConfig;
    private final ExperimentConfig experimentConfig;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    /**
     * Saves individual classifiers input options into database.
     */
    public void saveClassifiersOptions() {
        if (StringUtils.isEmpty(experimentConfig.getIndividualClassifiersStoragePath())) {
            logAndThrowError("Classifiers input options directory doesn't specified.", log);
        }
        File classifiersOptionsDir = new File(getClass().getClassLoader().getResource(
                experimentConfig.getIndividualClassifiersStoragePath()).getFile());
        Collection<File> modelFiles = FileUtils.listFiles(classifiersOptionsDir, null, true);
        if (CollectionUtils.isEmpty(modelFiles)) {
            logAndThrowError("Classifiers input options directory is empty.", log);
        } else {
            int version = classifierOptionsDatabaseModelRepository.findLatestVersion();
            List<ClassifierOptionsDatabaseModel> latestOptions =
                    classifierOptionsDatabaseModelRepository.findAllByVersion(version);
            List<ClassifierOptionsDatabaseModel> newOptions = createClassifiersOptions(modelFiles, ++version);
            if (CollectionUtils.isEmpty(latestOptions) || latestOptions.size() != newOptions.size() ||
                    !latestOptions.containsAll(newOptions)) {
                log.info("Saving new classifiers input options with version {}.", version);
                classifierOptionsDatabaseModelRepository.saveAll(newOptions);
            }
        }
    }

    /**
     * Finds the last classifiers options configs.
     *
     * @return {@link ClassifierOptionsDatabaseModel} list
     */
    @Cacheable(CacheNames.CLASSIFIERS_CACHE_NAME)
    public List<ClassifierOptionsDatabaseModel> findLastClassifiersOptions() {
        log.info("Starting to read classifiers input options configs from database");
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModelList =
                classifierOptionsDatabaseModelRepository.findAllByVersion(
                        classifierOptionsDatabaseModelRepository.findLatestVersion());
        log.info("{} classifiers input options configs has been successfully read from database.",
                classifierOptionsDatabaseModelList.size());
        return classifierOptionsDatabaseModelList;
    }

    @Override
    public Page<ClassifierOptionsDatabaseModel> getNextPage(PageRequestDto pageRequestDto) {
        int lastVersion = classifierOptionsDatabaseModelRepository.findLatestVersion();
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), ClassifierOptionsDatabaseModel_.CREATION_DATE,
                pageRequestDto.isAscending());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return classifierOptionsDatabaseModelRepository.findAllByVersion(lastVersion,
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    private List<ClassifierOptionsDatabaseModel> createClassifiersOptions(Collection<File> modelFiles, int version) {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels = new ArrayList<>(modelFiles.size());
        for (File modelFile : modelFiles) {
            try {
                classifierOptionsDatabaseModels.add(
                        createClassifierOptions(objectMapper.readValue(modelFile, ClassifierOptions.class), version));
            } catch (IOException ex) {
                logAndThrowError(String.format("There was an error while parsing json file '%s': %s",
                        modelFile.getAbsolutePath(),
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
            String config = objectMapper.writeValueAsString(classifierOptions);
            classifierOptionsDatabaseModel.setConfigMd5Hash(
                    DigestUtils.md5DigestAsHex(config.getBytes(StandardCharsets.UTF_8)));
            classifierOptionsDatabaseModel.setConfig(config);
            classifierOptionsDatabaseModel.setCreationDate(LocalDateTime.now());
            return classifierOptionsDatabaseModel;
        } catch (IOException ex) {
            logAndThrowError(String.format("There was an error while parsing object [%s]: %s", classifierOptions,
                    ex.getMessage()),
                    log);
        }
        return classifierOptionsDatabaseModel;
    }

}
