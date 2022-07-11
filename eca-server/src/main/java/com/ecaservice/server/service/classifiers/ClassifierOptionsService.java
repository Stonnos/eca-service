package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.UserService;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.config.audit.AuditCodes.ADD_CLASSIFIER_OPTIONS;
import static com.ecaservice.server.config.audit.AuditCodes.DELETE_CLASSIFIER_OPTIONS;
import static com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel_.CREATION_DATE;
import static com.ecaservice.server.util.ClassifierOptionsHelper.createClassifierOptionsDatabaseModel;
import static com.ecaservice.server.util.ClassifierOptionsHelper.isEnsembleClassifierOptions;

/**
 * Classifier options service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ClassifierOptionsService {

    private final AppProperties appProperties;
    private final UserService userService;
    private final ClassifierOptionsProcessor classifierOptionsProcessor;
    private final ClassifiersTemplateProvider classifiersTemplateProvider;
    private final ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;
    private final ClassifiersConfigurationHistoryService classifiersConfigurationHistoryService;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    /**
     * Saves new classifier options for specified configuration.
     *
     * @param configurationId   - configuration id
     * @param classifierOptions - classifier options
     */
    @Audit(value = ADD_CLASSIFIER_OPTIONS, correlationIdKey = "#configurationId")
    @Transactional
    public ClassifierOptionsDto saveClassifierOptions(long configurationId,
                                                      @Valid ClassifierOptions classifierOptions) {
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
        classifiersConfigurationHistoryService.saveAddClassifierOptionsAction(saved);
        return internalPopulateClassifierOptions(saved);
    }

    /**
     * Deletes classifier options with specified id.
     *
     * @param id - classifier options id
     * @return deleted classifier options entity
     */
    @Audit(value = DELETE_CLASSIFIER_OPTIONS, correlationIdKey = "#result.configuration.id")
    @Transactional
    public ClassifierOptionsDatabaseModel deleteOptions(long id) {
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
        classifiersConfigurationHistoryService.saveRemoveClassifierOptionsAction(classifierOptionsDatabaseModel);
        return classifierOptionsDatabaseModel;
    }

    /**
     * Gets classifiers options with specified filtering params.
     *
     * @param configurationId - configuration id
     * @param pageRequestDto  - page request dto
     * @return classifiers options page
     */
    public PageDto<ClassifierOptionsDto> getNextPage(long configurationId, PageRequestDto pageRequestDto) {
        log.info("Gets classifiers configuration [{}] options next page: {}", configurationId, pageRequestDto);
        var classifiersConfiguration = getConfigurationById(configurationId);
        var sort = buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        var pageSize = Integer.min(pageRequestDto.getSize(), appProperties.getMaxPageSize());
        var classifierOptionsPage =
                classifierOptionsDatabaseModelRepository.findAllByConfiguration(classifiersConfiguration,
                        PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
        var classifierOptionsDtoList = classifierOptionsPage.getContent()
                .stream()
                .map(this::internalPopulateClassifierOptions)
                .collect(Collectors.toList());
        log.info("Configuration [{}] options page [{} of {}] with size [{}] has been fetched for page request [{}]",
                configurationId, classifierOptionsPage.getNumber(), classifierOptionsPage.getTotalPages(),
                classifierOptionsPage.getNumberOfElements(), pageRequestDto);
        return PageDto.of(classifierOptionsDtoList, pageRequestDto.getPage(), classifierOptionsPage.getTotalElements());
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
        var latestOptions = classifierOptionsDatabaseModelRepository.findAllByConfigurationOrderByCreationDateDesc(
                classifiersConfiguration);
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

    private ClassifierOptionsDto internalPopulateClassifierOptions(
            ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        var classifierOptionsDto = classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModel);
        var inputOptions = classifierOptionsProcessor.processInputOptions(classifierOptionsDto.getConfig());
        var classifierFormTemplate =
                classifiersTemplateProvider.getClassifierTemplateByClass(classifierOptionsDto.getOptionsName());
        classifierOptionsDto.setOptionsDescription(classifierFormTemplate.getTemplateTitle());
        classifierOptionsDto.setInputOptions(inputOptions);
        return classifierOptionsDto;
    }
}
