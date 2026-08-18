package com.ecaservice.server.service.classifiers;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.core.message.template.service.MessageTemplateProcessor;
import com.ecaservice.server.filter.ClassifiersConfigurationHistoryFilter;
import com.ecaservice.server.mapping.ClassifiersConfigurationHistoryMapper;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationActionType;
import com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.UserService;
import com.ecaservice.web.dto.model.ClassifiersConfigurationHistoryDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity_.CREATED_AT;
import static com.ecaservice.server.model.entity.FilterTemplateType.CLASSIFIERS_CONFIGURATION_HISTORY;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIERS_CONFIGURATION_PARAM;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIER_INPUT_OPTIONS_DETAILS;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIER_OPTIONS_DESCRIPTION;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIER_OPTIONS_ID;
import static com.ecaservice.server.util.ClassifierOptionsHelper.getCommaSeparatedOptions;

/**
 * Classifiers configuration history service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class ClassifiersConfigurationHistoryService {

    private final UserService userService;
    private final FilterTemplateService filterTemplateService;
    private final ClassifiersConfigurationHistoryMapper classifiersConfigurationHistoryMapper;
    private final MessageTemplateProcessor messageTemplateProcessor;
    private final ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    private final ClassifiersFormTemplateProvider classifiersFormTemplateProvider;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;
    private final ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;

    /**
     * Saves created classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveCreateConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        Map<String, Object> messageParams =
                Collections.singletonMap(CLASSIFIERS_CONFIGURATION_PARAM, classifiersConfiguration);
        saveHistoryEntity(ClassifiersConfigurationActionType.CREATE_CONFIGURATION, classifiersConfiguration,
                messageParams);
    }

    /**
     * Saves updated classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveUpdateConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        Map<String, Object> messageParams =
                Collections.singletonMap(CLASSIFIERS_CONFIGURATION_PARAM, classifiersConfiguration);
        saveHistoryEntity(ClassifiersConfigurationActionType.UPDATE_CONFIGURATION, classifiersConfiguration,
                messageParams);
    }

    /**
     * Saves active classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveSetActiveConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        saveHistoryEntity(ClassifiersConfigurationActionType.SET_ACTIVE, classifiersConfiguration,
                Collections.emptyMap());
    }

    /**
     * Saves deactivate classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveDeactivateConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        saveHistoryEntity(ClassifiersConfigurationActionType.DEACTIVATE, classifiersConfiguration,
                Collections.emptyMap());
    }

    /**
     * Saves added classifiers options to history.
     *
     * @param classifierOptionsDatabaseModel - classifier options entity
     */
    public void saveAddClassifierOptionsAction(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        var messageParams = buildAddClassifierOptionsParams(classifierOptionsDatabaseModel);
        saveHistoryEntity(ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS,
                classifierOptionsDatabaseModel.getConfiguration(), messageParams);
    }

    /**
     * Saves added classifiers options to history.
     *
     * @param classifierOptionsDatabaseModels - classifier options entity
     */
    public void saveAddClassifierOptionsAction(List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels) {
        log.info("Starting to save classifier options list with size [{}] to history",
                classifierOptionsDatabaseModels.size());
        var classifierOptionsHistory = classifierOptionsDatabaseModels
                .stream()
                .map(classifierOptionsDatabaseModel -> createHistoryEntity(
                        ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS,
                        classifierOptionsDatabaseModel.getConfiguration(),
                        buildAddClassifierOptionsParams(classifierOptionsDatabaseModel)))
                .collect(Collectors.toList());
        classifiersConfigurationHistoryRepository.saveAll(classifierOptionsHistory);
        log.info("Classifier options list with size [{}] has been saved to history",
                classifierOptionsDatabaseModels.size());
    }

    /**
     * Saves removed classifiers options to history.
     *
     * @param classifierOptionsDatabaseModel - classifier options entity
     */
    public void saveRemoveClassifierOptionsAction(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        var messageParams = buildClassifierOptionsParams(classifierOptionsDatabaseModel);
        saveHistoryEntity(ClassifiersConfigurationActionType.REMOVE_CLASSIFIER_OPTIONS,
                classifierOptionsDatabaseModel.getConfiguration(), messageParams);
    }

    /**
     * Deletes classifiers configuration history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void deleteHistory(ClassifiersConfiguration classifiersConfiguration) {
        log.info("Deletes to remove classifiers configuration [{}] history", classifiersConfiguration.getId());
        var deleted = classifiersConfigurationHistoryRepository.deleteAllByConfiguration(classifiersConfiguration);
        log.info("[{}] history rows has been deleted from classifiers configuration [{}]", deleted,
                classifiersConfiguration.getId());
    }

    /**
     * Gets classifiers configuration history with specified filtering params.
     *
     * @param configurationId - configuration id
     * @param pageRequestDto  - page request dto
     * @return classifiers configuration history page
     */
    public PageDto<ClassifiersConfigurationHistoryDto> getNextPage(long configurationId,
                                                                   @ValidPageRequest(
                                                                           filterTemplateName = CLASSIFIERS_CONFIGURATION_HISTORY)
                                                                   PageRequestDto pageRequestDto) {
        log.info("Gets classifiers configuration [{}] history next page: {}", configurationId, pageRequestDto);
        var classifiersConfiguration = classifiersConfigurationRepository.findById(configurationId)
                .orElseThrow(() -> new EntityNotFoundException(ClassifiersConfiguration.class, configurationId));
        var globalFilterFields =
                filterTemplateService.getGlobalFilterFields(CLASSIFIERS_CONFIGURATION_HISTORY);
        var sort = buildSort(pageRequestDto.getSortFields(), CREATED_AT, true);
        var filter = new ClassifiersConfigurationHistoryFilter(classifiersConfiguration,
                pageRequestDto.getSearchQuery(), globalFilterFields, pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var nextPage =
                classifiersConfigurationHistoryRepository.findAll(filter, pageRequest);
        var classifiersConfigurationHistoryDtoList = classifiersConfigurationHistoryMapper.map(nextPage.getContent());
        log.info("Configurations history page [{} of {}] with size [{}] has been fetched for page request [{}]",
                nextPage.getNumber(), nextPage.getTotalPages(), nextPage.getNumberOfElements(), pageRequestDto);
        return PageDto.of(classifiersConfigurationHistoryDtoList, pageRequestDto.getPage(),
                nextPage.getTotalElements());
    }

    private Map<String, Object> buildClassifierOptionsParams(
            ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        Map<String, Object> messageParams = new HashMap<>();
        var classifierFormTemplate = classifiersFormTemplateProvider.getClassifierTemplateByClass(
                classifierOptionsDatabaseModel.getOptionsName());
        messageParams.put(CLASSIFIER_OPTIONS_ID, classifierOptionsDatabaseModel.getId());
        messageParams.put(CLASSIFIER_OPTIONS_DESCRIPTION, classifierFormTemplate.getTemplateTitle());
        return messageParams;
    }

    private Map<String, Object> buildAddClassifierOptionsParams(
            ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        var messageParams = buildClassifierOptionsParams(classifierOptionsDatabaseModel);
        var classifierInfoDto =
                classifierOptionsInfoProcessor.processClassifierInfo(classifierOptionsDatabaseModel.getConfig());
        String commaSeparatedOptions = getCommaSeparatedOptions(classifierInfoDto);
        messageParams.put(CLASSIFIER_INPUT_OPTIONS_DETAILS, commaSeparatedOptions);
        return messageParams;
    }

    private ClassifiersConfigurationHistoryEntity createHistoryEntity(ClassifiersConfigurationActionType actionType,
                                                                      ClassifiersConfiguration classifiersConfiguration,
                                                                      Map<String, Object> messageParams) {
        var classifiersConfigurationHistory = new ClassifiersConfigurationHistoryEntity();
        classifiersConfigurationHistory.setActionType(actionType);
        String messageText = messageTemplateProcessor.process(actionType.name(), messageParams);
        classifiersConfigurationHistory.setMessageText(messageText);
        classifiersConfigurationHistory.setConfiguration(classifiersConfiguration);
        classifiersConfigurationHistory.setCreatedBy(userService.getCurrentUser());
        classifiersConfigurationHistory.setCreatedAt(LocalDateTime.now());
        return classifiersConfigurationHistory;
    }

    private void saveHistoryEntity(ClassifiersConfigurationActionType actionType,
                                   ClassifiersConfiguration classifiersConfiguration,
                                   Map<String, Object> messageParams) {
        log.info("Starting to save classifiers configuration [{}] action [{}] to history",
                classifiersConfiguration.getId(), actionType);
        var classifiersConfigurationHistory =
                createHistoryEntity(actionType, classifiersConfiguration, messageParams);
        classifiersConfigurationHistoryRepository.save(classifiersConfigurationHistory);
        log.info("Classifiers configuration [{}] action [{}] has been saved to history",
                classifiersConfiguration.getId(), actionType);
    }
}
