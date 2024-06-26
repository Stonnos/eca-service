package com.ecaservice.server.service.classifiers;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.common.web.exception.InvalidOperationException;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.lock.config.CoreLockAutoConfiguration;
import com.ecaservice.core.lock.metrics.LockMeterService;
import com.ecaservice.core.message.template.service.MessageTemplateProcessor;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.mapping.ClassifierOptionsDatabaseModelMapperImpl;
import com.ecaservice.server.mapping.ClassifiersConfigurationHistoryMapperImpl;
import com.ecaservice.server.mapping.ClassifiersConfigurationMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationActionType;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.report.model.ClassifiersConfigurationBean;
import com.ecaservice.server.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.UserService;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ecaservice.server.model.entity.BaseEntity_.CREATION_DATE;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifiersConfigurationService} functionality
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@Import({ClassifiersConfigurationServiceImpl.class, ClassifiersConfigurationMapperImpl.class, AppProperties.class,
        DateTimeConverter.class, ClassifierOptionsDatabaseModelMapperImpl.class,
        CoreLockAutoConfiguration.class,
        ClassifiersConfigurationHistoryService.class, ClassifiersConfigurationHistoryMapperImpl.class})
class ClassifiersConfigurationServiceTest extends AbstractJpaTest {

    private static final int NUM_THREADS = 4;
    private static final String TEST_CONFIG = "test_config";
    private static final String TEST_CONFIGURATION_NAME = "TestConfiguration";
    private static final String TEST_CONFIGURATION_UPDATED_NAME = "UpdatedTestName";
    private static final String USER_NAME = "user";
    private static final long ID = 1L;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String MESSAGE = "message";
    private static final String TEMPLATE_TITLE = "title";

    @Autowired
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Autowired
    private ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;
    @Autowired
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Autowired
    private ClassifiersConfigurationService classifiersConfigurationService;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private UserService userService;
    @MockBean
    private LockMeterService lockMeterService;
    @MockBean
    private ClassifiersFormTemplateProvider classifiersFormTemplateProvider;
    @MockBean
    private MessageTemplateProcessor messageTemplateProcessor;

    @Override
    public void init() {
        when(userService.getCurrentUser()).thenReturn(USER_NAME);
        var formTemplate = new FormTemplateDto();
        formTemplate.setTemplateTitle(TEMPLATE_TITLE);
        when(classifiersFormTemplateProvider.getClassifierTemplateByClass(anyString())).thenReturn(formTemplate);
        when(messageTemplateProcessor.process(anyString(), anyMap())).thenReturn(MESSAGE);
    }

    @Override
    public void deleteAll() {
        classifierOptionsDatabaseModelRepository.deleteAll();
        classifiersConfigurationHistoryRepository.deleteAll();
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
        verifyClassifiersConfigurationHistory(actual, ClassifiersConfigurationActionType.CREATE_CONFIGURATION);
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
        var classifiersConfigurationHistory =
                TestHelperUtils.createClassifiersConfigurationHistory(classifiersConfiguration,
                        ClassifiersConfigurationActionType.CREATE_CONFIGURATION, LocalDateTime.now());
        classifiersConfigurationHistoryRepository.save(classifiersConfigurationHistory);
        classifiersConfigurationService.delete(classifiersConfiguration.getId());
        ClassifiersConfiguration actualConfiguration =
                classifiersConfigurationRepository.findById(classifiersConfiguration.getId()).orElse(null);
        assertThat(actualConfiguration).isNull();
        assertThat(classifierOptionsDatabaseModelRepository.count()).isZero();
        assertThat(classifiersConfigurationHistoryRepository.count()).isZero();
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
        verifyClassifiersConfigurationHistory(actualActive, ClassifiersConfigurationActionType.SET_ACTIVE);
        verifyClassifiersConfigurationHistory(lastActive, ClassifiersConfigurationActionType.DEACTIVATE);
    }

    @Test
    void testConcurrentSetActiveConfiguration() throws InterruptedException {
        saveConfiguration(true, false);
        List<ClassifiersConfiguration> newActiveConfigurations = newArrayList();
        IntStream.range(0, NUM_THREADS).forEach(i -> newActiveConfigurations.add(saveConfiguration(false, false)));
        final CountDownLatch countDownLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            var newActive = newActiveConfigurations.get(i);
            executorService.submit(() -> {
                try {
                    classifiersConfigurationService.setActive(newActive.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdownNow();
        long countActive = classifiersConfigurationRepository.findAll().stream()
                .filter(ClassifiersConfiguration::isActive)
                .count();
        assertThat(countActive).isOne();
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
    void testSetAlreadyActiveConfiguration() {
        ClassifiersConfiguration active = saveConfiguration(true, false);
        assertThrows(InvalidOperationException.class, () -> classifiersConfigurationService.setActive(active.getId()));
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
        when(filterTemplateService.getGlobalFilterFields(FilterTemplateType.CLASSIFIERS_CONFIGURATION))
                .thenReturn(Collections.emptyList());
        ClassifiersConfiguration firstConfiguration = saveConfiguration(true, true);
        ClassifiersConfiguration secondConfiguration = saveConfiguration(false, false);
        classifierOptionsDatabaseModelRepository.save(
                TestHelperUtils.createClassifierOptionsDatabaseModel(TEST_CONFIG, secondConfiguration));
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE,
                Collections.singletonList(new SortFieldRequestDto(CREATION_DATE, false)), null,
                Collections.emptyList());
        PageDto<ClassifiersConfigurationDto> configurationsPage =
                classifiersConfigurationService.getClassifiersConfigurations(pageRequestDto);
        assertThat(configurationsPage).isNotNull();
        assertThat(configurationsPage.getPage()).isEqualTo(pageRequestDto.getPage());
        assertThat(configurationsPage.getTotalCount()).isEqualTo(2);
        assertThat(configurationsPage.getContent()).isNotNull().hasSize(2);

        //Assert configurations dto
        Map<Long, ClassifiersConfigurationDto> classifiersConfigurationDtoMap = configurationsPage.getContent()
                .stream()
                .collect(Collectors.toMap(ClassifiersConfigurationDto::getId, Function.identity()));
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
        verifyClassifiersConfigurationHistory(copy, ClassifiersConfigurationActionType.CREATE_CONFIGURATION);
        assertThat(classifiersConfigurationHistoryRepository.count()).isEqualTo(actualOptionsCopies.size() + 1);
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

    private void verifyClassifiersConfigurationHistory(ClassifiersConfiguration classifiersConfiguration,
                                                       ClassifiersConfigurationActionType expectedActionType) {
        var classifiersConfigurationHistoryList = classifiersConfigurationHistoryRepository.findAll();
        var classifiersConfigurationHistory = classifiersConfigurationHistoryList.stream()
                .filter(configurationHistoryEntity ->
                        configurationHistoryEntity.getConfiguration().getId().equals(classifiersConfiguration.getId())
                                && expectedActionType.equals(configurationHistoryEntity.getActionType()))
                .findFirst()
                .orElse(null);
        assertThat(classifiersConfigurationHistory).isNotNull();
        assertThat(classifiersConfigurationHistory.getConfiguration().getId())
                .isEqualTo(classifiersConfiguration.getId());
        assertThat(classifiersConfigurationHistory.getCreatedBy()).isEqualTo(USER_NAME);
        assertThat(classifiersConfigurationHistory.getActionType()).isEqualTo(expectedActionType);
    }
}
