package com.ecaservice.service.classifiers;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.exception.EntityNotFoundException;
import com.ecaservice.filter.ClassifiersConfigurationFilter;
import com.ecaservice.mapping.ClassifiersConfigurationMapper;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.projections.ClassifiersOptionsStatistics;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.PageRequestService;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ecaservice.model.entity.ClassifiersConfiguration_.CREATED;

/**
 * Classifiers configuration service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersConfigurationService implements PageRequestService<ClassifiersConfiguration> {

    private final FilterService filterService;
    private final ClassifiersConfigurationMapper classifiersConfigurationMapper;
    private final CommonConfig commonConfig;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    /**
     * Saves new classifiers configuration.
     *
     * @param configurationDto - create classifiers configuration dto
     */
    public ClassifiersConfiguration save(CreateClassifiersConfigurationDto configurationDto) {
        ClassifiersConfiguration classifiersConfiguration = classifiersConfigurationMapper.map(configurationDto);
        classifiersConfiguration.setCreated(LocalDateTime.now());
        ClassifiersConfiguration savedConfiguration = classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been saved", savedConfiguration.getConfigurationName());
        return savedConfiguration;
    }

    /**
     * Updates classifiers configuration.
     *
     * @param configurationDto - update classifiers configuration dto
     */
    public void update(UpdateClassifiersConfigurationDto configurationDto) {
        ClassifiersConfiguration classifiersConfiguration = getById(configurationDto.getId());
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
        ClassifiersConfiguration classifiersConfiguration = getById(id);
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
    public void setActive(long id) {
        ClassifiersConfiguration classifiersConfiguration = getById(id);
        ClassifiersConfiguration activeConfiguration =
                classifiersConfigurationRepository.findFirstByActiveTrue().orElseThrow(
                        () -> new IllegalStateException("Can't find active classifiers configuration!"));
        if (!classifiersConfiguration.getId().equals(activeConfiguration.getId())) {
            activeConfiguration.setActive(false);
            classifiersConfiguration.setActive(true);
            classifiersConfigurationRepository.saveAll(Arrays.asList(classifiersConfiguration, activeConfiguration));
            log.info("Classifiers configuration [{}] has been set as active.", classifiersConfiguration.getId());
        }
    }

    /**
     * Gets classifiers configuration by id.
     *
     * @param id - classifiers configuration id
     * @return classifiers configuration entity
     */
    public ClassifiersConfiguration getById(long id) {
        return classifiersConfigurationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ClassifiersConfiguration.class, id));
    }

    @Override
    public Page<ClassifiersConfiguration> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), CREATED, pageRequestDto.isAscending());
        List<String> globalFilterFields =
                filterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIERS_CONFIGURATION);
        ClassifiersConfigurationFilter filter = new ClassifiersConfigurationFilter(pageRequestDto.getSearchQuery(),
                globalFilterFields, pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
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
        Page<ClassifiersConfiguration> classifiersConfigurationsPage = getNextPage(pageRequestDto);
        List<ClassifiersConfigurationDto> configurationDtoList =
                classifiersConfigurationMapper.map(classifiersConfigurationsPage.getContent());
        if (classifiersConfigurationsPage.hasContent()) {
            List<Long> configurationsIds =
                    classifiersConfigurationsPage.getContent().stream().map(ClassifiersConfiguration::getId).collect(
                            Collectors.toList());
            List<ClassifiersOptionsStatistics> classifiersOptionsStatisticsList =
                    classifierOptionsDatabaseModelRepository.getClassifiersOptionsStatistics(configurationsIds);
            Map<Long, ClassifiersConfigurationDto> configurationDtoMap = configurationDtoList.stream().collect(
                    Collectors.toMap(ClassifiersConfigurationDto::getId, Function.identity()));
            classifiersOptionsStatisticsList.forEach(classifiersOptionsStatistics -> {
                ClassifiersConfigurationDto classifiersConfigurationDto =
                        configurationDtoMap.get(classifiersOptionsStatistics.getConfigurationId());
                classifiersConfigurationDto.setClassifiersOptionsCount(
                        classifiersOptionsStatistics.getClassifiersOptionsCount());
            });
        }
        return PageDto.of(configurationDtoList, pageRequestDto.getPage(),
                classifiersConfigurationsPage.getTotalElements());
    }
}
