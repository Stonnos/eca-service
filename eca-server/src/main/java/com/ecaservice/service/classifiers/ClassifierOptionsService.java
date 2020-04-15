package com.ecaservice.service.classifiers;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.exception.EntityNotFoundException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.util.SortUtils;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_.CREATION_DATE;
import static com.ecaservice.util.ClassifierOptionsHelper.createClassifierOptionsDatabaseModel;

/**
 * Classifier options service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsService {

    private final CommonConfig commonConfig;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    /**
     * Saves new classifier options for specified configuration.
     *
     * @param configurationId   - configuration id
     * @param classifierOptions - classifier options
     */
    @Transactional
    public void saveClassifierOptions(long configurationId, ClassifierOptions classifierOptions) {
        ClassifiersConfiguration classifiersConfiguration = getConfigurationById(configurationId);
        Assert.state(!classifiersConfiguration.isBuildIn(),
                "Can't add classifier options to build in configuration!");
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                createClassifierOptionsDatabaseModel(classifierOptions, classifiersConfiguration);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("New classifier options has been saved for configuration [{}]", configurationId);
    }

    /**
     * Deletes classifier options with specified id.
     *
     * @param id - classifier options id
     */
    @Transactional
    public void deleteOptions(long id) {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                classifierOptionsDatabaseModelRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException(ClassifierOptionsDatabaseModel.class, id));
        ClassifiersConfiguration classifiersConfiguration = classifierOptionsDatabaseModel.getConfiguration();
        Assert.state(!classifiersConfiguration.isBuildIn(),
                "Can't delete classifier options from build in configuration!");
        classifierOptionsDatabaseModelRepository.delete(classifierOptionsDatabaseModel);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("Classifier options with id [{}] has been deleted", classifierOptionsDatabaseModel.getId());
    }

    /**
     * Gets classifiers options with specified filtering params.
     *
     * @param configurationId - configuration id
     * @param pageRequestDto  - page request dto
     * @return classifiers options page
     */
    public Page<ClassifierOptionsDatabaseModel> getNextPage(long configurationId, PageRequestDto pageRequestDto) {
        ClassifiersConfiguration classifiersConfiguration = getConfigurationById(configurationId);
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return classifierOptionsDatabaseModelRepository.findAllByConfiguration(classifiersConfiguration,
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    /**
     * Finds classifiers options for active configuration.
     *
     * @return classifiers options list
     */
    public List<ClassifierOptionsDatabaseModel> getActiveClassifiersOptions() {
        ClassifiersConfiguration activeConfiguration =
                classifiersConfigurationRepository.findFirstByActiveTrue().orElseThrow(
                        () -> new IllegalStateException("Can't find active classifiers configuration!"));
        return classifierOptionsDatabaseModelRepository.findAllByConfiguration(activeConfiguration);
    }

    /**
     * Updates build in classifiers configuration with new classifiers options.
     *
     * @param classifiersConfiguration - classifiers configuration
     * @param newOptions               - new option list
     */
    @Transactional
    public void updateBuildInClassifiersConfiguration(ClassifiersConfiguration classifiersConfiguration,
                                                      List<ClassifierOptionsDatabaseModel> newOptions) {
        List<ClassifierOptionsDatabaseModel> latestOptions =
                classifierOptionsDatabaseModelRepository.findAllByConfiguration(classifiersConfiguration);
        if (CollectionUtils.isEmpty(latestOptions) || latestOptions.size() != newOptions.size() ||
                !newOptions.containsAll(latestOptions)) {
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
            if (CollectionUtils.isEmpty(latestOptions)) {
                classifiersConfiguration.setUpdated(LocalDateTime.now());
                classifiersConfigurationRepository.save(classifiersConfiguration);
            }
            log.info("New classifiers input options has been saved for build in configuration.");
        }
    }

    private ClassifiersConfiguration getConfigurationById(long configurationId) {
        return classifiersConfigurationRepository.findById(configurationId).orElseThrow(
                () -> new EntityNotFoundException(ClassifiersConfiguration.class, configurationId));
    }
}
