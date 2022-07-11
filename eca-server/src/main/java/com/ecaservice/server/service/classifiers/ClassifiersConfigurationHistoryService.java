package com.ecaservice.server.service.classifiers;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.filter.ClassifiersConfigurationHistoryFilter;
import com.ecaservice.server.mapping.ClassifiersConfigurationHistoryMapper;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationActionType;
import com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import com.ecaservice.web.dto.model.ClassifiersConfigurationHistoryDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity_.CREATED_AT;
import static com.ecaservice.server.service.message.template.MessageTemplateVariables.CLASSIFIERS_CONFIGURATION_PARAM;
import static com.ecaservice.server.service.message.template.MessageTemplateVariables.CLASSIFIER_OPTIONS_DESCRIPTION;
import static com.ecaservice.server.service.message.template.MessageTemplateVariables.CLASSIFIER_OPTIONS_ID;

/**
 * Classifiers configuration history service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersConfigurationHistoryService {

    private final AppProperties appProperties;
    private final UserService userService;
    private final FilterService filterService;
    private final ClassifiersConfigurationHistoryMapper classifiersConfigurationHistoryMapper;
    private final MessageTemplateProcessor messageTemplateProcessor;
    private final ClassifiersTemplateProvider classifiersTemplateProvider;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;
    private final ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;

    /**
     * Saves created classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveCreateConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        saveToHistory(ClassifiersConfigurationActionType.CREATE_CONFIGURATION, classifiersConfiguration,
                () -> Collections.singletonMap(CLASSIFIERS_CONFIGURATION_PARAM, classifiersConfiguration));
    }

    /**
     * Saves updated classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveUpdateConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        saveToHistory(ClassifiersConfigurationActionType.UPDATE_CONFIGURATION, classifiersConfiguration,
                () -> Collections.singletonMap(CLASSIFIERS_CONFIGURATION_PARAM, classifiersConfiguration));
    }

    /**
     * Saves active classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveSetActiveConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        saveToHistory(ClassifiersConfigurationActionType.SET_ACTIVE, classifiersConfiguration, Collections::emptyMap);
    }

    /**
     * Saves added classifiers options to history.
     *
     * @param classifierOptionsDatabaseModel - classifier options entity
     */
    public void saveAddClassifierOptionsAction(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        saveToHistory(ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS,
                classifierOptionsDatabaseModel.getConfiguration(),
                () -> buildClassifierOptionsParams(classifierOptionsDatabaseModel));
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
                .map(classifierOptionsDatabaseModel -> createHistory(
                        ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS,
                        classifierOptionsDatabaseModel.getConfiguration(),
                        () -> buildClassifierOptionsParams(classifierOptionsDatabaseModel)))
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
        saveToHistory(ClassifiersConfigurationActionType.REMOVE_CLASSIFIER_OPTIONS,
                classifierOptionsDatabaseModel.getConfiguration(),
                () -> buildClassifierOptionsParams(classifierOptionsDatabaseModel));
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
                                                                   PageRequestDto pageRequestDto) {
        log.info("Gets classifiers configuration [{}] history next page: {}", configurationId, pageRequestDto);
        var classifiersConfiguration = classifiersConfigurationRepository.findById(configurationId)
                .orElseThrow(() -> new EntityNotFoundException(ClassifiersConfiguration.class, configurationId));
        var globalFilterFields =
                filterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIERS_CONFIGURATION_HISTORY.name());
        var sort = buildSort(pageRequestDto.getSortField(), CREATED_AT, pageRequestDto.isAscending());
        var filter = new ClassifiersConfigurationHistoryFilter(classifiersConfiguration,
                pageRequestDto.getSearchQuery(), globalFilterFields, pageRequestDto.getFilters());
        var pageSize = Integer.min(pageRequestDto.getSize(), appProperties.getMaxPageSize());
        var nextPage = classifiersConfigurationHistoryRepository.findAll(filter,
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
        var classifiersConfigurationHistoryDtoList = classifiersConfigurationHistoryMapper.map(nextPage.getContent());
        log.info("Configurations history page [{} of {}] with size [{}] has been fetched for page request [{}]",
                nextPage.getNumber(), nextPage.getTotalPages(), nextPage.getNumberOfElements(), pageRequestDto);
        return PageDto.of(classifiersConfigurationHistoryDtoList, pageRequestDto.getPage(),
                nextPage.getTotalElements());
    }

    private Map<String, Object> buildClassifierOptionsParams(
            ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        var classifierFormTemplate = classifiersTemplateProvider.getClassifierTemplateByClass(
                classifierOptionsDatabaseModel.getOptionsName());
        return Map.of(
                CLASSIFIER_OPTIONS_ID, classifierOptionsDatabaseModel.getId(),
                CLASSIFIER_OPTIONS_DESCRIPTION, classifierFormTemplate.getTemplateTitle()
        );
    }

    private ClassifiersConfigurationHistoryEntity createHistory(ClassifiersConfigurationActionType actionType,
                                                                ClassifiersConfiguration classifiersConfiguration,
                                                                Supplier<Map<String, Object>> messageParamsSupplier) {
        var classifiersConfigurationHistory = new ClassifiersConfigurationHistoryEntity();
        classifiersConfigurationHistory.setActionType(actionType);
        String messageText = messageTemplateProcessor.process(actionType.name(), messageParamsSupplier.get());
        classifiersConfigurationHistory.setMessageText(messageText);
        classifiersConfigurationHistory.setConfiguration(classifiersConfiguration);
        classifiersConfigurationHistory.setCreatedBy(userService.getCurrentUser());
        classifiersConfigurationHistory.setCreatedAt(LocalDateTime.now());
        return classifiersConfigurationHistory;
    }

    private void saveToHistory(ClassifiersConfigurationActionType actionType,
                               ClassifiersConfiguration classifiersConfiguration,
                               Supplier<Map<String, Object>> messageParamsSupplier) {
        log.info("Starting to save classifiers configuration [{}] action [{}] to history",
                classifiersConfiguration.getId(), actionType);
        var classifiersConfigurationHistory =
                createHistory(actionType, classifiersConfiguration, messageParamsSupplier);
        classifiersConfigurationHistoryRepository.save(classifiersConfigurationHistory);
        log.info("Classifiers configuration [{}] action [{}] has been saved to history",
                classifiersConfiguration.getId(), actionType);
    }
}
