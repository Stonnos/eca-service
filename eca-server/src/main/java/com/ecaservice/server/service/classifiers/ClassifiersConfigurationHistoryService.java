package com.ecaservice.server.service.classifiers;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.filter.ClassifiersConfigurationHistoryFilter;
import com.ecaservice.server.mapping.ClassifiersConfigurationHistoryMapper;
import com.ecaservice.server.model.entity.*;
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

import java.time.LocalDateTime;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity_.CREATED_AT;

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
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;
    private final ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;

    /**
     * Saves created classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveCreateConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        saveToHistory(ClassifiersConfigurationActionType.CREATE_CONFIGURATION, classifiersConfiguration);
    }

    /**
     * Saves active classifiers configuration to history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     */
    public void saveSetActiveConfigurationAction(ClassifiersConfiguration classifiersConfiguration) {
        saveToHistory(ClassifiersConfigurationActionType.SET_ACTIVE, classifiersConfiguration);
    }

    /**
     * Saves added classifiers options to history.
     *
     * @param classifierOptionsDatabaseModel - classifier options entity
     */
    public void saveAddClassifierOptions(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        saveToHistory(ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS,
                classifierOptionsDatabaseModel.getConfiguration());
    }

    /**
     * Saves removed classifiers options to history.
     *
     * @param classifierOptionsDatabaseModel - classifier options entity
     */
    public void saveRemoveClassifierOptions(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        saveToHistory(ClassifiersConfigurationActionType.REMOVE_CLASSIFIER_OPTIONS,
                classifierOptionsDatabaseModel.getConfiguration());
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
        var filter = new ClassifiersConfigurationHistoryFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        var pageSize = Integer.min(pageRequestDto.getSize(), appProperties.getMaxPageSize());
        var nextPage = classifiersConfigurationHistoryRepository.findAllByConfiguration(classifiersConfiguration,
                filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
        var classifiersConfigurationHistoryDtoList = classifiersConfigurationHistoryMapper.map(nextPage.getContent());
        log.info("Configurations history page [{} of {}] with size [{}] has been fetched for page request [{}]",
                nextPage.getNumber(), nextPage.getTotalPages(), nextPage.getNumberOfElements(), pageRequestDto);
        return PageDto.of(classifiersConfigurationHistoryDtoList, pageRequestDto.getPage(), nextPage.getTotalElements());
    }

    private void saveToHistory(ClassifiersConfigurationActionType actionType,
                               ClassifiersConfiguration classifiersConfiguration) {
        log.info("Starting to save classifiers configuration [{}] action [{}] to history",
                classifiersConfiguration.getId(), actionType);
        var classifiersConfigurationHistory = new ClassifiersConfigurationHistoryEntity();
        classifiersConfigurationHistory.setActionType(actionType);
        classifiersConfigurationHistory.setMessageText(actionType.getDescription());
        classifiersConfigurationHistory.setConfiguration(classifiersConfiguration);
        classifiersConfigurationHistory.setCreatedBy(userService.getCurrentUser());
        classifiersConfigurationHistory.setCreatedAt(LocalDateTime.now());
        classifiersConfigurationHistoryRepository.save(classifiersConfigurationHistory);
        log.info("Classifiers configuration [{}] action [{}] has been saved to history",
                classifiersConfiguration.getId(), actionType);
    }
}
