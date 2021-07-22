package com.ecaservice.service.classifiers;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.UserService;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ecaservice.config.audit.AuditCodes.ADD_CLASSIFIER_OPTIONS;
import static com.ecaservice.config.audit.AuditCodes.DELETE_CLASSIFIER_OPTIONS;
import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_.CREATION_DATE;
import static com.ecaservice.util.ClassifierOptionsHelper.createClassifierOptionsDatabaseModel;
import static com.ecaservice.util.ClassifierOptionsHelper.isEnsembleClassifierOptions;

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
    private final UserService userService;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    /**
     * Saves new classifier options for specified configuration.
     *
     * @param configurationId   - configuration id
     * @param classifierOptions - classifier options
     */
    @Audit(ADD_CLASSIFIER_OPTIONS)
    @Transactional
    public ClassifierOptionsDatabaseModel saveClassifierOptions(long configurationId,
                                                                ClassifierOptions classifierOptions) {
        var classifiersConfiguration = getConfigurationById(configurationId);
        Assert.state(!classifiersConfiguration.isBuildIn(),
                "Can't add classifier options to build in configuration!");
        Assert.state(!isEnsembleClassifierOptions(classifierOptions), "Can't save ensemble classifier options!");
        var classifierOptionsDatabaseModel =
                createClassifierOptionsDatabaseModel(classifierOptions, classifiersConfiguration);
        classifierOptionsDatabaseModel.setCreatedBy(userService.getCurrentUser());
        var saved = classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("New classifier options [{}, id {}] has been saved for configuration [{}]", saved.getOptionsName(),
                saved.getId(), configurationId);
        return saved;
    }

    /**
     * Deletes classifier options with specified id.
     *
     * @param id - classifier options id
     */
    @Audit(DELETE_CLASSIFIER_OPTIONS)
    @Transactional
    public void deleteOptions(long id) {
        log.info("Starting to delete classifier options [{}]", id);
        var classifierOptionsDatabaseModel = classifierOptionsDatabaseModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ClassifierOptionsDatabaseModel.class, id));
        var classifiersConfiguration = classifierOptionsDatabaseModel.getConfiguration();
        Assert.state(!classifiersConfiguration.isBuildIn(),
                "Can't delete classifier options from build in configuration!");
        Assert.state(hasMoreThanOneOptionsForActiveConfiguration(classifiersConfiguration), String.format(
                "Can't delete classifier options [%d], because classifiers configuration is active and has only one classifier options!",
                classifierOptionsDatabaseModel.getId()));
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
        var classifiersConfiguration = getConfigurationById(configurationId);
        var sort = buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        var pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return classifierOptionsDatabaseModelRepository.findAllByConfiguration(classifiersConfiguration,
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    /**
     * Finds classifiers options for active configuration.
     *
     * @return classifiers options list
     */
    public List<ClassifierOptionsDatabaseModel> getActiveClassifiersOptions() {
        var activeConfiguration = classifiersConfigurationRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("Can't find active classifiers configuration!"));
        var classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAllByConfigurationOrderByCreationDateDesc(
                        activeConfiguration);
        log.info("Fetched active classifiers configuration with name [{}], id [{}], options size [{}]!",
                activeConfiguration.getConfigurationName(), activeConfiguration.getId(),
                classifierOptionsDatabaseModels.size());
        return classifierOptionsDatabaseModels;
    }

    /**
     * Updates build in classifiers configuration with new classifiers options.
     *
     * @param classifiersConfiguration - classifiers configuration
     * @param newOptions               - new option set
     */
    @Transactional
    public void updateBuildInClassifiersConfiguration(ClassifiersConfiguration classifiersConfiguration,
                                                      Set<ClassifierOptionsDatabaseModel> newOptions) {
        Assert.state(classifiersConfiguration.isBuildIn(), "Expected build in configuration!");
        Assert.notEmpty(newOptions, "New classifiers options list must be not empty!");
        var latestOptions = classifierOptionsDatabaseModelRepository.findAllByConfigurationOrderByCreationDateDesc(classifiersConfiguration);
        if (CollectionUtils.isEmpty(latestOptions) || latestOptions.size() != newOptions.size() ||
                !newOptions.containsAll(latestOptions)) {
            var oldOptionsToDelete = latestOptions.stream()
                    .filter(options -> !newOptions.contains(options))
                    .collect(Collectors.toList());
            var newOptionsToSave = newOptions.stream()
                    .filter(options -> !latestOptions.contains(options))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(oldOptionsToDelete)) {
                classifierOptionsDatabaseModelRepository.deleteAll(oldOptionsToDelete);
            }
            if (!CollectionUtils.isEmpty(newOptionsToSave)) {
                classifierOptionsDatabaseModelRepository.saveAll(newOptionsToSave);
            }
            if (!CollectionUtils.isEmpty(latestOptions) &&
                    (!CollectionUtils.isEmpty(oldOptionsToDelete) || !CollectionUtils.isEmpty(newOptionsToSave))) {
                classifiersConfiguration.setUpdated(LocalDateTime.now());
                classifiersConfigurationRepository.save(classifiersConfiguration);
            }
            log.info("New classifiers input options has been saved for build in configuration.");
        }
    }

    private ClassifiersConfiguration getConfigurationById(long configurationId) {
        return classifiersConfigurationRepository.findById(configurationId)
                .orElseThrow(() -> new EntityNotFoundException(ClassifiersConfiguration.class, configurationId));
    }

    private boolean hasMoreThanOneOptionsForActiveConfiguration(ClassifiersConfiguration classifiersConfiguration) {
        return !classifiersConfiguration.isActive() ||
                classifierOptionsDatabaseModelRepository.countByConfiguration(classifiersConfiguration) > 1L;
    }

}
