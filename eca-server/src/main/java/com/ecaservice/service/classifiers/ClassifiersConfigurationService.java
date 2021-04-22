package com.ecaservice.service.classifiers;

import com.ecaservice.aspect.annotation.Locked;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.filter.ClassifiersConfigurationFilter;
import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.mapping.ClassifiersConfigurationMapper;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.report.model.ClassifiersConfigurationBean;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.PageRequestService;
import com.ecaservice.service.UserService;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.util.SortUtils;
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
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

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
     */
    public ClassifiersConfiguration save(CreateClassifiersConfigurationDto configurationDto) {
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
    public void update(UpdateClassifiersConfigurationDto configurationDto) {
        var classifiersConfiguration = getById(configurationDto.getId());
        classifiersConfigurationMapper.update(configurationDto, classifiersConfiguration);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been updated", classifiersConfiguration.getId());
    }

    /**
     * Deletes classifiers configuration by specified id.
     *
     * @param id - classifiers configuration id
     */
    public void delete(long id) {
        var classifiersConfiguration = getById(id);
        Assert.state(!classifiersConfiguration.isBuildIn(),
                String.format("Can't delete build in configuration [%d]!", id));
        Assert.state(!classifiersConfiguration.isActive(),
                String.format("Can't delete active configuration [%d]!", id));
        classifiersConfigurationRepository.delete(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been deleted", id);
    }

    /**
     * Sets classifiers configuration as active.
     *
     * @param id - configuration id
     */
    @Locked(lockName = "setActiveClassifiersConfiguration")
    public void setActive(long id) {
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
        var sort = SortUtils.buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        var globalFilterFields =
                filterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIERS_CONFIGURATION);
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
}
