package com.ecaservice.service.classifiers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapperImpl;
import com.ecaservice.mapping.ClassifiersConfigurationMapperImpl;
import com.ecaservice.mapping.DateTimeConverter;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.report.model.ClassifiersConfigurationBean;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.UserService;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ecaservice.model.entity.BaseEntity_.CREATION_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifiersConfigurationService} functionality
 *
 * @author Roman Batygin
 */
@Import({ClassifiersConfigurationService.class, ClassifiersConfigurationMapperImpl.class, CommonConfig.class,
        DateTimeConverter.class, ClassifierOptionsDatabaseModelMapperImpl.class})
class ClassifiersConfigurationServiceTest extends AbstractJpaTest {

    private static final String TEST_CONFIG = "test_config";
    private static final String TEST_CONFIGURATION_NAME = "TestConfiguration";
    private static final String TEST_CONFIGURATION_UPDATED_NAME = "UpdatedTestName";
    private static final String USER_NAME = "user";
    private static final long ID = 1L;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Inject
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Inject
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Inject
    private ClassifiersConfigurationService classifiersConfigurationService;
    @MockBean
    private FilterService filterService;
    @MockBean
    private UserService userService;

    @Override
    public void init() {
        when(userService.getCurrentUser()).thenReturn(USER_NAME);
    }

    @Override
    public void deleteAll() {
        classifierOptionsDatabaseModelRepository.deleteAll();
        classifiersConfigurationRepository.deleteAll();
    }

    @Test
    void testSaveClassifiersConfiguration() {
        CreateClassifiersConfigurationDto configurationDto = new CreateClassifiersConfigurationDto();
        configurationDto.setConfigurationName(TEST_CONFIGURATION_NAME);
        classifiersConfigurationService.save(configurationDto);
        List<ClassifiersConfiguration> configurations = classifiersConfigurationRepository.findAll();
        assertThat(configurations).hasSize(1);
        ClassifiersConfiguration actual = configurations.iterator().next();
        assertThat(actual.getConfigurationName()).isEqualTo(configurationDto.getConfigurationName());
        assertThat(actual.getCreatedBy()).isEqualTo(USER_NAME);
        assertThat(actual.getCreationDate()).isNotNull();
        assertThat(actual.isBuildIn()).isFalse();
    }

    @Test
    void testUpdateNotExistingConfiguration() {
        UpdateClassifiersConfigurationDto updateClassifiersConfigurationDto = new UpdateClassifiersConfigurationDto();
        updateClassifiersConfigurationDto.setId(ID);
        assertThrows(EntityNotFoundException.class,
                () -> classifiersConfigurationService.update(updateClassifiersConfigurationDto));
    }

    @Test
    void testUpdateClassifiersConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = saveConfiguration(true, false);
        assertThat(classifiersConfiguration.getUpdated()).isNull();
        assertThat(classifiersConfiguration.getConfigurationName()).isEqualTo(TEST_CONFIGURATION_NAME);
        UpdateClassifiersConfigurationDto updateClassifiersConfigurationDto = new UpdateClassifiersConfigurationDto();
        updateClassifiersConfigurationDto.setConfigurationName(TEST_CONFIGURATION_UPDATED_NAME);
        updateClassifiersConfigurationDto.setId(classifiersConfiguration.getId());
        classifiersConfigurationService.update(updateClassifiersConfigurationDto);
        List<ClassifiersConfiguration> configurations = classifiersConfigurationRepository.findAll();
        assertThat(configurations).hasSize(1);
        ClassifiersConfiguration actual = configurations.iterator().next();
        assertThat(actual.getConfigurationName()).isEqualTo(updateClassifiersConfigurationDto.getConfigurationName());
        assertThat(actual.getUpdated()).isNotNull();
    }

    @Test
    void testDeleteNotExistingConfiguration() {
        assertThrows(EntityNotFoundException.class, () -> classifiersConfigurationService.delete(ID));
    }

    @Test
    void testDeleteConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = saveConfiguration(false, false);
        classifiersConfigurationService.delete(classifiersConfiguration.getId());
        ClassifiersConfiguration actualConfiguration =
                classifiersConfigurationRepository.findById(classifiersConfiguration.getId()).orElse(null);
        assertThat(actualConfiguration).isNull();
        assertThat(classifierOptionsDatabaseModelRepository.count()).isZero();
    }

    @Test
    void testDeleteActiveConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = saveConfiguration(true, false);
        assertThrows(IllegalStateException.class,
                () -> classifiersConfigurationService.delete(classifiersConfiguration.getId()));
    }

    @Test
    void testDeleteBuildInConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = saveConfiguration(true, false);
        assertThrows(IllegalStateException.class,
                () -> classifiersConfigurationService.delete(classifiersConfiguration.getId()));
    }

    @Test
    void testSetActiveNotExistingConfiguration() {
        assertThrows(EntityNotFoundException.class, () -> classifiersConfigurationService.setActive(ID));
    }

    @Test
    void testSetActiveNotExistingActiveConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = saveConfiguration(false, false);
        assertThrows(IllegalStateException.class,
                () -> classifiersConfigurationService.setActive(classifiersConfiguration.getId()));
    }

    @Test
    void testSetActiveConfiguration() {
        ClassifiersConfiguration lastActive = saveConfiguration(true, false);
        ClassifiersConfiguration newActive = saveConfiguration(false, false);
        classifiersConfigurationService.setActive(newActive.getId());
        ClassifiersConfiguration actualActive =
                classifiersConfigurationRepository.findById(newActive.getId()).orElse(null);
        ClassifiersConfiguration actualNotActive =
                classifiersConfigurationRepository.findById(lastActive.getId()).orElse(null);
        assertThat(actualActive).isNotNull();
        assertThat(actualActive.isActive()).isTrue();
        assertThat(actualNotActive).isNotNull();
        assertThat(actualNotActive.isActive()).isFalse();
    }

    @Test
    void testSetActiveConfigurationWithEmptyClassifiersOptions() {
        saveConfiguration(true, false);
        ClassifiersConfiguration newActive = new ClassifiersConfiguration();
        newActive.setBuildIn(false);
        newActive.setActive(false);
        newActive.setCreationDate(LocalDateTime.now());
        newActive.setConfigurationName(TEST_CONFIGURATION_NAME);
        classifiersConfigurationRepository.save(newActive);
        assertThrows(IllegalStateException.class, () -> classifiersConfigurationService.setActive(newActive.getId()));
    }

    @Test
    void testGetClassifiersConfigurationDetailsNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> classifiersConfigurationService.getClassifiersConfigurationDetails(ID));
    }

    @Test
    void testGetClassifiersConfigurationDetails() {
        ClassifiersConfiguration configuration = saveConfiguration(true, true);
        ClassifiersConfigurationDto classifiersConfigurationDto =
                classifiersConfigurationService.getClassifiersConfigurationDetails(configuration.getId());
        assertThat(classifiersConfigurationDto).isNotNull();
        assertThat(classifiersConfigurationDto.getId()).isEqualTo(configuration.getId());
        assertThat(classifiersConfigurationDto.getConfigurationName()).isEqualTo(configuration.getConfigurationName());
        assertThat(classifiersConfigurationDto.getClassifiersOptionsCount()).isEqualTo(
                classifierOptionsDatabaseModelRepository.countByConfiguration(configuration));
    }

    @Test
    void testGetClassifiersConfigurations() {
        when(filterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIERS_CONFIGURATION.name()))
                .thenReturn(Collections.emptyList());
        ClassifiersConfiguration firstConfiguration = saveConfiguration(true, true);
        ClassifiersConfiguration secondConfiguration = saveConfiguration(false, false);
        classifierOptionsDatabaseModelRepository.save(
                TestHelperUtils.createClassifierOptionsDatabaseModel(TEST_CONFIG, secondConfiguration));
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, CREATION_DATE, false, null, Collections.emptyList());
        PageDto<ClassifiersConfigurationDto> configurationsPage =
                classifiersConfigurationService.getClassifiersConfigurations(pageRequestDto);
        assertThat(configurationsPage).isNotNull();
        assertThat(configurationsPage.getPage()).isEqualTo(pageRequestDto.getPage());
        assertThat(configurationsPage.getTotalCount()).isEqualTo(2);
        assertThat(configurationsPage.getContent()).isNotNull().hasSize(2);

        //Assert configurations dto
        Map<Long, ClassifiersConfigurationDto> classifiersConfigurationDtoMap =
                configurationsPage.getContent().stream().collect(
                        Collectors.toMap(ClassifiersConfigurationDto::getId, Function.identity()));
        ClassifiersConfigurationDto actualFirst = classifiersConfigurationDtoMap.get(firstConfiguration.getId());
        assertThat(actualFirst.getId()).isEqualTo(firstConfiguration.getId());
        assertThat(actualFirst.getClassifiersOptionsCount()).isEqualTo(2);
        ClassifiersConfigurationDto actualSecond = classifiersConfigurationDtoMap.get(secondConfiguration.getId());
        assertThat(actualSecond.getId()).isEqualTo(secondConfiguration.getId());
        assertThat(actualSecond.getClassifiersOptionsCount()).isEqualTo(3);
    }

    @Test
    void testGetClassifiersConfigurationReport() {
        ClassifiersConfiguration configuration = saveConfiguration(true, true);
        ClassifiersConfigurationBean classifiersConfigurationBean =
                classifiersConfigurationService.getClassifiersConfigurationReport(configuration.getId());
        assertThat(classifiersConfigurationBean).isNotNull();
        assertThat(classifiersConfigurationBean.getConfigurationName()).isEqualTo(configuration.getConfigurationName());
        long expectedCount = classifierOptionsDatabaseModelRepository.countByConfiguration(configuration);
        assertThat(classifiersConfigurationBean.getClassifiersOptionsCount()).isEqualTo(expectedCount);
    }

    @Test
    void testCopyClassifiersConfiguration() {
        var classifiersConfiguration = saveConfiguration(true, true);
        assertThat(classifiersConfiguration.getUpdated()).isNull();
        assertThat(classifiersConfiguration.getConfigurationName()).isEqualTo(TEST_CONFIGURATION_NAME);
        var updateClassifiersConfigurationDto = new UpdateClassifiersConfigurationDto();
        updateClassifiersConfigurationDto.setConfigurationName(TEST_CONFIGURATION_UPDATED_NAME);
        updateClassifiersConfigurationDto.setId(classifiersConfiguration.getId());
        var copy = classifiersConfigurationService.copy(updateClassifiersConfigurationDto);
        var actualCopy = classifiersConfigurationRepository.findById(copy.getId()).orElse(null);
        assertThat(actualCopy).isNotNull();
        assertThat(actualCopy.getConfigurationName()).isEqualTo(
                updateClassifiersConfigurationDto.getConfigurationName());
        assertThat(actualCopy.getCreatedBy()).isNotNull();
        assertThat(actualCopy.getCreationDate()).isNotNull();
        assertThat(actualCopy.isBuildIn()).isFalse();
        assertThat(actualCopy.isActive()).isFalse();
        var expectedOptionsCopies = classifierOptionsDatabaseModelRepository.findAllByConfigurationOrderByCreationDate(
                classifiersConfiguration);
        var actualOptionsCopies =
                classifierOptionsDatabaseModelRepository.findAllByConfigurationOrderByCreationDate(actualCopy);
        assertThat(actualOptionsCopies).hasSameSizeAs(expectedOptionsCopies);
    }

    private ClassifiersConfiguration saveConfiguration(boolean active, boolean buildIn) {
        ClassifiersConfiguration classifiersConfiguration = TestHelperUtils.createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(buildIn);
        classifiersConfiguration.setActive(active);
        classifiersConfiguration.setConfigurationName(TEST_CONFIGURATION_NAME);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        classifierOptionsDatabaseModelRepository.saveAll(Arrays.asList(
                TestHelperUtils.createClassifierOptionsDatabaseModel(TEST_CONFIG, classifiersConfiguration),
                TestHelperUtils.createClassifierOptionsDatabaseModel(TEST_CONFIG, classifiersConfiguration)
        ));
        return classifiersConfiguration;
    }
}
