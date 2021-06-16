package com.ecaservice.service.classifiers;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.filter.ClassifiersConfigurationFilter;
import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.mapping.ClassifiersConfigurationMapper;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.report.model.ClassifiersConfigurationBean;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.PageRequestService;
import com.ecaservice.service.UserService;
import com.ecaservice.core.filter.service.FilterService;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ecaservice.config.audit.AuditCodes.ADD_CONFIGURATION;
import static com.ecaservice.config.audit.AuditCodes.COPY_CONFIGURATION;
import static com.ecaservice.config.audit.AuditCodes.DELETE_CONFIGURATION;
import static com.ecaservice.config.audit.AuditCodes.RENAME_CONFIGURATION;
import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.model.entity.BaseEntity_.CREATION_DATE;

/**
 * Classifiers configuration service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersConfigurationService implements PageRequestService<ClassifiersConfiguration> {

    private final UserService userService;
    private final FilterService filterService;
    private final ClassifiersConfigurationMapper classifiersConfigurationMapper;
    private final ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;
    private final CommonConfig commonConfig;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    /**
     * Saves new classifiers configuration.
     *
     * @param configurationDto - create classifiers configuration dto
     * @return classifiers configuration entity
     */
    @Audit(ADD_CONFIGURATION)
    public ClassifiersConfiguration save(CreateClassifiersConfigurationDto configurationDto) {
        log.info("Starting to save new classifiers configuration [{}]", configurationDto.getConfigurationName());
        var classifiersConfiguration = classifiersConfigurationMapper.map(configurationDto);
        classifiersConfiguration.setCreatedBy(userService.getCurrentUser());
        classifiersConfiguration.setCreationDate(LocalDateTime.now());
        var savedConfiguration = classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been saved", savedConfiguration.getConfigurationName());
        return savedConfiguration;
    }

    /**
     * Updates classifiers configuration.
     *
     * @param configurationDto - update classifiers configuration dto
     */
    @Audit(RENAME_CONFIGURATION)
    public void update(UpdateClassifiersConfigurationDto configurationDto) {
        log.info("Starting to update classifiers configuration [{}] with new name [{}]", configurationDto.getId(),
                configurationDto.getConfigurationName());
        var classifiersConfiguration = getById(configurationDto.getId());
        classifiersConfigurationMapper.update(configurationDto, classifiersConfiguration);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been updated with name [{}]", classifiersConfiguration.getId(),
                classifiersConfiguration.getConfigurationName());
    }

    /**
     * Deletes classifiers configuration by specified id.
     *
     * @param id - classifiers configuration id
     */
    @Audit(DELETE_CONFIGURATION)
    public void delete(long id) {
        log.info("Starting to delete classifiers configuration [{}]", id);
        var classifiersConfiguration = getById(id);
        Assert.state(!classifiersConfiguration.isBuildIn(),
                String.format("Can't delete build in configuration [%d]!", id));
        Assert.state(!classifiersConfiguration.isActive(),
                String.format("Can't delete active configuration [%d]!", id));
        classifiersConfigurationRepository.delete(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been deleted", id);
    }

    /**
     * Creates classifiers configuration copy. Created copy will always be not build in and not active.
     *
     * @param configurationDto - configuration data
     * @return classifiers configuration entity
     */
    @Audit(COPY_CONFIGURATION)
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
        copyClassifiersOptions(classifiersConfiguration, classifiersConfigurationCopy);
        log.info("Classifiers configuration [{}] copy [{}] has been created with new id [{}]",
                classifiersConfiguration.getId(), classifiersConfigurationCopy.getConfigurationName(),
                classifiersConfigurationCopy.getId());
        return classifiersConfigurationCopy;
    }

    /**
     * Sets classifiers configuration as active.
     *
     * @param id - configuration id
     */
    @Locked(lockName = "setActiveClassifiersConfiguration")
    public void setActive(long id) {
        log.info("Request to set classifiers configuration [{}] as active", id);
        var classifiersConfiguration = getById(id);
        var activeConfiguration = classifiersConfigurationRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("Can't find previous active classifiers configuration!"));
        Assert.state(classifierOptionsDatabaseModelRepository.countByConfiguration(classifiersConfiguration) > 0L,
                String.format("Can't set configuration [%d] as active, because its has no one classifiers options!",
                        classifiersConfiguration.getId()));
        if (!classifiersConfiguration.getId().equals(activeConfiguration.getId())) {
            activeConfiguration.setActive(false);
            classifiersConfiguration.setActive(true);
            classifiersConfigurationRepository.saveAll(Arrays.asList(classifiersConfiguration, activeConfiguration));
            log.info("Classifiers configuration [{}] has been set as active.", classifiersConfiguration.getId());
        }
    }

    @Override
    public Page<ClassifiersConfiguration> getNextPage(PageRequestDto pageRequestDto) {
        var sort = buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        var globalFilterFields =
                filterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIERS_CONFIGURATION.name());
        var filter = new ClassifiersConfigurationFilter(pageRequestDto.getSearchQuery(),
                globalFilterFields, pageRequestDto.getFilters());
        var pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return classifiersConfigurationRepository.findAll(filter,
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    /**
     * Gets classifiers configurations dto models page
     *
     * @param pageRequestDto - page request object
     * @return classifiers configurations dto models page
     */
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

    /**
     * Gets classifiers configuration details by id.
     *
     * @param id - configuration id
     * @return classifiers configuration dto
     */
    public ClassifiersConfigurationDto getClassifiersConfigurationDetails(long id) {
        var classifiersConfiguration = getById(id);
        var classifiersConfigurationDto = classifiersConfigurationMapper.map(classifiersConfiguration);
        classifiersConfigurationDto.setClassifiersOptionsCount(
                classifierOptionsDatabaseModelRepository.countByConfiguration(classifiersConfiguration));
        return classifiersConfigurationDto;
    }

    /**
     * Gets classifiers configuration report by id.
     *
     * @param id - configuration id
     * @return classifiers configuration report
     */
    public ClassifiersConfigurationBean getClassifiersConfigurationReport(long id) {
        var classifiersConfiguration = getById(id);
        var classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAllByConfigurationOrderByCreationDateDesc(
                        classifiersConfiguration);
        var classifiersConfigurationBean = classifiersConfigurationMapper.mapToBean(classifiersConfiguration);
        classifiersConfigurationBean.setClassifiersOptionsCount(classifierOptionsDatabaseModels.size());
        classifiersConfigurationBean.setClassifiersOptions(
                classifierOptionsDatabaseModelMapper.mapToBeans(classifierOptionsDatabaseModels));
        return classifiersConfigurationBean;
    }

    private ClassifiersConfiguration getById(long id) {
        return classifiersConfigurationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ClassifiersConfiguration.class, id));
    }

    private void copyClassifiersOptions(ClassifiersConfiguration classifiersConfiguration,
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
    }
}
