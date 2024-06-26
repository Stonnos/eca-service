package com.ecaservice.server.service.classifiers;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.common.web.exception.InvalidOperationException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.filter.ClassifiersConfigurationFilter;
import com.ecaservice.server.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.server.mapping.ClassifiersConfigurationMapper;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.report.model.ClassifierOptionsBean;
import com.ecaservice.server.report.model.ClassifiersConfigurationBean;
import com.ecaservice.server.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.UserService;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.config.audit.AuditCodes.ADD_CONFIGURATION;
import static com.ecaservice.server.config.audit.AuditCodes.COPY_CONFIGURATION;
import static com.ecaservice.server.config.audit.AuditCodes.DELETE_CONFIGURATION;
import static com.ecaservice.server.config.audit.AuditCodes.RENAME_CONFIGURATION;
import static com.ecaservice.server.config.audit.AuditCodes.SET_ACTIVE_CONFIGURATION;
import static com.ecaservice.server.model.entity.BaseEntity_.CREATION_DATE;
import static com.ecaservice.server.model.entity.FilterTemplateType.CLASSIFIERS_CONFIGURATION;

/**
 * Classifiers configuration service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service("classifiersConfigurationServiceImpl")
@RequiredArgsConstructor
public class ClassifiersConfigurationServiceImpl implements ClassifiersConfigurationService {

    private final UserService userService;
    private final FilterTemplateService filterTemplateService;
    private final ClassifiersConfigurationMapper classifiersConfigurationMapper;
    private final ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;
    private final ClassifiersConfigurationHistoryService classifiersConfigurationHistoryService;
    private final ClassifiersFormTemplateProvider classifiersFormTemplateProvider;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    @Override
    @Audit(value = ADD_CONFIGURATION, correlationIdKey = "#result.id")
    @Transactional
    public ClassifiersConfiguration save(CreateClassifiersConfigurationDto configurationDto) {
        log.info("Starting to save new classifiers configuration [{}]", configurationDto.getConfigurationName());
        var classifiersConfiguration = classifiersConfigurationMapper.map(configurationDto);
        classifiersConfiguration.setCreatedBy(userService.getCurrentUser());
        classifiersConfiguration.setCreationDate(LocalDateTime.now());
        var savedConfiguration = classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been saved", savedConfiguration.getConfigurationName());
        classifiersConfigurationHistoryService.saveCreateConfigurationAction(savedConfiguration);
        return savedConfiguration;
    }

    @Override
    @Audit(value = RENAME_CONFIGURATION, correlationIdKey = "#configurationDto.id")
    public ClassifiersConfiguration update(UpdateClassifiersConfigurationDto configurationDto) {
        log.info("Starting to update classifiers configuration [{}] with new name [{}]", configurationDto.getId(),
                configurationDto.getConfigurationName());
        var classifiersConfiguration = getById(configurationDto.getId());
        classifiersConfigurationMapper.update(configurationDto, classifiersConfiguration);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been updated with name [{}]", classifiersConfiguration.getId(),
                classifiersConfiguration.getConfigurationName());
        classifiersConfigurationHistoryService.saveUpdateConfigurationAction(classifiersConfiguration);
        return classifiersConfiguration;
    }

    @Override
    @Audit(value = DELETE_CONFIGURATION, correlationIdKey = "#id")
    @Transactional
    public void delete(long id) {
        log.info("Starting to delete classifiers configuration [{}]", id);
        var classifiersConfiguration = getById(id);
        Assert.state(!classifiersConfiguration.isBuildIn(),
                String.format("Can't delete build in configuration [%d]!", id));
        Assert.state(!classifiersConfiguration.isActive(),
                String.format("Can't delete active configuration [%d]!", id));
        classifiersConfigurationHistoryService.deleteHistory(classifiersConfiguration);
        classifiersConfigurationRepository.delete(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been deleted", id);
    }

    @Override
    @Audit(value = COPY_CONFIGURATION, correlationIdKey = "#configurationDto.id")
    @Audit(value = ADD_CONFIGURATION, correlationIdKey = "#result.id")
    @Transactional
    public ClassifiersConfiguration copy(UpdateClassifiersConfigurationDto configurationDto) {
        log.info("Starting to create classifiers configuration [{}] copy with name [{}]",
                configurationDto.getId(), configurationDto.getConfigurationName());
        var classifiersConfiguration = getById(configurationDto.getId());
        var classifiersConfigurationCopy = new ClassifiersConfiguration();
        classifiersConfigurationCopy.setConfigurationName(configurationDto.getConfigurationName());
        classifiersConfigurationCopy.setCreatedBy(userService.getCurrentUser());
        classifiersConfigurationCopy.setCreationDate(LocalDateTime.now());
        classifiersConfigurationRepository.save(classifiersConfigurationCopy);
        var classifiersOptionsCopies = copyClassifiersOptions(classifiersConfiguration, classifiersConfigurationCopy);
        log.info("Classifiers configuration [{}] copy [{}] has been created with new id [{}]",
                classifiersConfiguration.getId(), classifiersConfigurationCopy.getConfigurationName(),
                classifiersConfigurationCopy.getId());
        classifiersConfigurationHistoryService.saveCreateConfigurationAction(classifiersConfigurationCopy);
        classifiersConfigurationHistoryService.saveAddClassifierOptionsAction(classifiersOptionsCopies);
        return classifiersConfigurationCopy;
    }

    @Override
    @Audit(value = SET_ACTIVE_CONFIGURATION, correlationIdKey = "#id")
    @Locked(lockName = "setActiveClassifiersConfiguration")
    @Transactional
    public ClassifiersConfiguration setActive(long id) {
        log.info("Request to set classifiers configuration [{}] as active", id);
        var classifiersConfiguration = getById(id);
        var activeConfiguration = classifiersConfigurationRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("Can't find previous active classifiers configuration!"));
        Assert.state(classifierOptionsDatabaseModelRepository.countByConfiguration(classifiersConfiguration) > 0L,
                String.format("Can't set configuration [%d] as active, because its has no one classifiers options!",
                        classifiersConfiguration.getId()));
        if (classifiersConfiguration.getId().equals(activeConfiguration.getId())) {
            throw new InvalidOperationException(String.format("Classifiers configuration [%d] is already active!", id));
        } else {
            activeConfiguration.setActive(false);
            classifiersConfiguration.setActive(true);
            classifiersConfigurationRepository.saveAll(Arrays.asList(classifiersConfiguration, activeConfiguration));
            log.info("Classifiers configuration [{}] has been set as active.", classifiersConfiguration.getId());
            classifiersConfigurationHistoryService.saveSetActiveConfigurationAction(classifiersConfiguration);
            classifiersConfigurationHistoryService.saveDeactivateConfigurationAction(activeConfiguration);
        }
        return classifiersConfiguration;
    }

    @Override
    public Page<ClassifiersConfiguration> getNextPage(PageRequestDto pageRequestDto) {
        log.info("Gets classifiers configurations next page: {}", pageRequestDto);
        var sort = buildSort(pageRequestDto.getSortFields(), CREATION_DATE, true);
        var globalFilterFields =
                filterTemplateService.getGlobalFilterFields(CLASSIFIERS_CONFIGURATION);
        var filter = new ClassifiersConfigurationFilter(pageRequestDto.getSearchQuery(),
                globalFilterFields, pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var nextPage = classifiersConfigurationRepository.findAll(filter, pageRequest);
        log.info("Classifiers configurations page [{} of {}] with size [{}] has been fetched for page request [{}]",
                nextPage.getNumber(), nextPage.getTotalPages(), nextPage.getNumberOfElements(), pageRequestDto);
        return nextPage;
    }

    @Override
    public PageDto<ClassifiersConfigurationDto> getClassifiersConfigurations(PageRequestDto pageRequestDto) {
        var classifiersConfigurationsPage = getNextPage(pageRequestDto);
        var configurationDtoList = classifiersConfigurationMapper.map(classifiersConfigurationsPage.getContent());
        if (classifiersConfigurationsPage.hasContent()) {
            var configurationsIds = classifiersConfigurationsPage.getContent()
                    .stream()
                    .map(ClassifiersConfiguration::getId)
                    .collect(Collectors.toList());
            var classifiersOptionsStatisticsList =
                    classifierOptionsDatabaseModelRepository.getClassifiersOptionsStatistics(configurationsIds);
            var configurationDtoMap = configurationDtoList.stream()
                    .collect(Collectors.toMap(ClassifiersConfigurationDto::getId, Function.identity()));
            classifiersOptionsStatisticsList.forEach(classifiersOptionsStatistics -> {
                var classifiersConfigurationDto =
                        configurationDtoMap.get(classifiersOptionsStatistics.getConfigurationId());
                classifiersConfigurationDto.setClassifiersOptionsCount(
                        classifiersOptionsStatistics.getClassifiersOptionsCount());
            });
        }
        return PageDto.of(configurationDtoList, pageRequestDto.getPage(),
                classifiersConfigurationsPage.getTotalElements());
    }

    @Override
    public ClassifiersConfigurationDto getClassifiersConfigurationDetails(long id) {
        var classifiersConfiguration = getById(id);
        var classifiersConfigurationDto = classifiersConfigurationMapper.map(classifiersConfiguration);
        classifiersConfigurationDto.setClassifiersOptionsCount(
                classifierOptionsDatabaseModelRepository.countByConfiguration(classifiersConfiguration));
        return classifiersConfigurationDto;
    }

    @Override
    public ClassifiersConfigurationBean getClassifiersConfigurationReport(long id) {
        log.info("Starting to get classifiers configuration [{}] report data", id);
        var classifiersConfiguration = getById(id);
        var classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAllByConfigurationOrderByCreationDateDesc(
                        classifiersConfiguration);
        var classifiersConfigurationBean = classifiersConfigurationMapper.mapToBean(classifiersConfiguration);
        classifiersConfigurationBean.setClassifiersOptionsCount(classifierOptionsDatabaseModels.size());
        var classifierOptionsBeans = classifierOptionsDatabaseModels
                .stream()
                .map(this::internalPopulateClassifierOptionsBean)
                .collect(Collectors.toList());
        classifiersConfigurationBean.setClassifiersOptions(classifierOptionsBeans);
        log.info("Classifiers configuration [{}] report data has been fetched", id);
        return classifiersConfigurationBean;
    }

    @Override
    public ClassifiersConfiguration getById(long id) {
        return classifiersConfigurationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ClassifiersConfiguration.class, id));
    }

    private List<ClassifierOptionsDatabaseModel> copyClassifiersOptions(
            ClassifiersConfiguration classifiersConfiguration,
            ClassifiersConfiguration classifiersConfigurationCopy) {
        log.info("Starting to copy classifiers options for configuration [{}]", classifiersConfiguration.getId());
        var classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAllByConfigurationOrderByCreationDate(
                        classifiersConfiguration);
        log.info("Got [{}] classifiers options for copy", classifierOptionsDatabaseModels.size());
        var currentUser = userService.getCurrentUser();
        var classifierOptionsCopies = classifierOptionsDatabaseModels.stream()
                .map(classifierOptionsDatabaseModel -> {
                    var classifierOptionsCopy = new ClassifierOptionsDatabaseModel();
                    classifierOptionsCopy.setOptionsName(classifierOptionsDatabaseModel.getOptionsName());
                    classifierOptionsCopy.setConfig(classifierOptionsDatabaseModel.getConfig());
                    classifierOptionsCopy.setConfigMd5Hash(classifierOptionsDatabaseModel.getConfigMd5Hash());
                    classifierOptionsCopy.setCreatedBy(currentUser);
                    classifierOptionsCopy.setCreationDate(LocalDateTime.now());
                    classifierOptionsCopy.setConfiguration(classifiersConfigurationCopy);
                    return classifierOptionsCopy;
                })
                .collect(Collectors.toList());
        classifierOptionsDatabaseModelRepository.saveAll(classifierOptionsCopies);
        log.info("[{}] classifiers options has been copied for configuration [{}]", classifierOptionsCopies.size(),
                classifiersConfiguration.getId());
        return classifierOptionsCopies;
    }

    private ClassifierOptionsBean internalPopulateClassifierOptionsBean(
            ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel) {
        var classifierOptionsBean = classifierOptionsDatabaseModelMapper.mapToBean(classifierOptionsDatabaseModel);
        var classifierFormTemplate = classifiersFormTemplateProvider.getClassifierTemplateByClass(
                classifierOptionsDatabaseModel.getOptionsName());
        classifierOptionsBean.setOptionsName(classifierFormTemplate.getTemplateTitle());
        return classifierOptionsBean;
    }
}
