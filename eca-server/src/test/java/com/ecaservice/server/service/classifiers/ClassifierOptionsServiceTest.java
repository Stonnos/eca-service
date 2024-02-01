package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.AdaBoostOptions;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.LogisticOptions;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.exception.EnsembleClassifierOptionsNotAllowedException;
import com.ecaservice.server.mapping.ClassifierOptionsDatabaseModelMapperImpl;
import com.ecaservice.server.mapping.ClassifiersConfigurationHistoryMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationActionType;
import com.ecaservice.server.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.InputOptionDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.ecaservice.server.TestHelperUtils.createClassifierOptionsDatabaseModel;
import static com.ecaservice.server.TestHelperUtils.createClassifiersConfiguration;
import static com.ecaservice.server.TestHelperUtils.createLogisticOptions;
import static com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel_.CREATION_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifierOptionsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({AppProperties.class, ClassifierOptionsService.class, ClassifierOptionsDatabaseModelMapperImpl.class,
        DateTimeConverter.class, ClassifiersConfigurationHistoryService.class,
        ClassifiersConfigurationHistoryMapperImpl.class})
class ClassifierOptionsServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final long ID = 1L;
    private static final String USER_NAME = "user";
    private static final String MESSAGE = "message";
    private static final String TEMPLATE_TITLE = "title";

    @Inject
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Inject
    private ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;
    @Inject
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    @MockBean
    private ClassifiersFormTemplateProvider classifiersFormTemplateProvider;
    @MockBean
    private MessageTemplateProcessor messageTemplateProcessor;
    @Inject
    private ClassifierOptionsService classifierOptionsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String optionsJson;

    @Override
    public void init() throws JsonProcessingException {
        optionsJson = objectMapper.writeValueAsString(createLogisticOptions());
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
    void testGetClassifiersOptionsPage() {
        when(classifierOptionsInfoProcessor.processInputOptions(any(ClassifierOptions.class)))
                .thenReturn(Collections.singletonList(new InputOptionDto()));
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(true);
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE,
                Collections.singletonList(new SortFieldRequestDto(CREATION_DATE, false)), null,
                Collections.emptyList());
        var classifierOptionsDatabaseModelPage =
                classifierOptionsService.getNextPage(classifierOptionsDatabaseModel.getConfiguration().getId(),
                        pageRequestDto);
        assertThat(classifierOptionsDatabaseModelPage).isNotNull();
        assertThat(classifierOptionsDatabaseModelPage.getTotalCount()).isOne();
        var classifierOptionsDto = classifierOptionsDatabaseModelPage.getContent().iterator().next();
        assertThat(classifierOptionsDto).isNotNull();
        assertThat(classifierOptionsDto.getInputOptions()).hasSize(1);
    }

    @Test
    void testGetClassifiersOptionsPageForNotExistingConfiguration() {
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE,
                Collections.singletonList(new SortFieldRequestDto(CREATION_DATE, false)), null,
                Collections.emptyList());
        assertThrows(EntityNotFoundException.class, () -> classifierOptionsService.getNextPage(ID, pageRequestDto));
    }

    @Test
    void testGetActiveOptionsForNotExistsConfiguration() {
        assertThrows(IllegalStateException.class, () -> classifierOptionsService.getActiveClassifiersOptions());
    }

    @Test
    void testGetActiveOptions() {
        saveClassifierOptions(true);
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsService.getActiveClassifiersOptions();
        assertThat(classifierOptionsDatabaseModels).hasSize(1);
    }

    @Test
    void testDeleteNotExistingOptions() {
        assertThrows(EntityNotFoundException.class, () -> classifierOptionsService.deleteOptions(ID));
    }

    @Test
    void testDeleteOptionsForActiveConfiguration() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(false);
        classifierOptionsDatabaseModelRepository.save(
                createClassifierOptionsDatabaseModel(optionsJson, classifierOptionsDatabaseModel.getConfiguration()));
        classifierOptionsService.deleteOptions(classifierOptionsDatabaseModel.getId());
        ClassifierOptionsDatabaseModel actualOptions =
                classifierOptionsDatabaseModelRepository.findById(classifierOptionsDatabaseModel.getId()).orElse(null);
        assertThat(actualOptions).isNull();
        ClassifiersConfiguration actualConfiguration = classifiersConfigurationRepository.findById(
                classifierOptionsDatabaseModel.getConfiguration().getId()).orElse(null);
        assertThat(actualConfiguration).isNotNull();
        assertThat(actualConfiguration.getUpdated()).isNotNull();
    }

    @Test
    void testDeleteOptionsForNotActiveConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setActive(false);
        classifiersConfiguration.setBuildIn(false);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = classifierOptionsDatabaseModelRepository.save(
                createClassifierOptionsDatabaseModel(optionsJson, classifiersConfiguration));
        classifierOptionsService.deleteOptions(classifierOptionsDatabaseModel.getId());
        ClassifierOptionsDatabaseModel actualOptions =
                classifierOptionsDatabaseModelRepository.findById(classifierOptionsDatabaseModel.getId()).orElse(null);
        assertThat(actualOptions).isNull();
        ClassifiersConfiguration actualConfiguration = classifiersConfigurationRepository.findById(
                classifierOptionsDatabaseModel.getConfiguration().getId()).orElse(null);
        assertThat(actualConfiguration).isNotNull();
        assertThat(actualConfiguration.getUpdated()).isNotNull();
        verifyClassifiersConfigurationHistory(classifiersConfiguration,
                ClassifiersConfigurationActionType.REMOVE_CLASSIFIER_OPTIONS);
    }

    @Test
    void testDeleteOptionsForActiveConfigurationWithSingleOption() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setActive(true);
        classifiersConfiguration.setBuildIn(false);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                createClassifierOptionsDatabaseModel(optionsJson, classifiersConfiguration);
        classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
        assertThrows(IllegalStateException.class,
                () -> classifierOptionsService.deleteOptions(classifierOptionsDatabaseModel.getId()));
    }

    @Test
    void testDeleteFromBuildInConfiguration() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(true);
        assertThrows(IllegalStateException.class,
                () -> classifierOptionsService.deleteOptions(classifierOptionsDatabaseModel.getId()));
    }

    @Test
    void testSaveOptionsForNotExistingClassifiersConfiguration() {
        assertThrows(EntityNotFoundException.class,
                () -> classifierOptionsService.saveClassifierOptions(ID, TestHelperUtils.createLogisticOptions()));
    }

    @Test
    void testSaveClassifierOptions() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(false);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        LogisticOptions logisticOptions = TestHelperUtils.createLogisticOptions();
        ClassifierOptionsDto classifierOptionsDto =
                classifierOptionsService.saveClassifierOptions(classifiersConfiguration.getId(), logisticOptions);
        ClassifierOptionsDatabaseModel actual =
                classifierOptionsDatabaseModelRepository.findById(classifierOptionsDto.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getOptionsName()).isEqualTo(logisticOptions.getClass().getSimpleName());
        assertThat(actual.getConfigMd5Hash()).isNotNull();
        assertThat(actual.getConfig()).isNotNull();
        assertThat(actual.getCreationDate()).isNotNull();
        assertThat(actual.getConfiguration().getUpdated()).isNotNull();
        assertThat(actual.getCreatedBy()).isEqualTo(USER_NAME);
        verifyClassifiersConfigurationHistory(actual.getConfiguration(),
                ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS);
    }

    @Test
    void testSaveClassifierOptionsToBuildInConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(true);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        LogisticOptions logisticOptions = TestHelperUtils.createLogisticOptions();
        assertThrows(IllegalStateException.class,
                () -> classifierOptionsService.saveClassifierOptions(classifiersConfiguration.getId(),
                        logisticOptions));
    }

    @Test
    void testSaveEnsembleClassifierOptions() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(false);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        AdaBoostOptions adaBoostOptions = TestHelperUtils.createAdaBoostOptions();
        assertThrows(EnsembleClassifierOptionsNotAllowedException.class,
                () -> classifierOptionsService.saveClassifierOptions(classifiersConfiguration.getId(),
                        adaBoostOptions));
    }

    @Test
    void testUpdateNotBuildInClassifiersConfigurationOptions() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(false);
        Set<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels = Sets.newHashSet(Collections.singletonList(
                createClassifierOptionsDatabaseModel(optionsJson, classifierOptionsDatabaseModel.getConfiguration())));
        assertThrows(IllegalStateException.class, () -> classifierOptionsService.updateBuildInClassifiersConfiguration(
                classifierOptionsDatabaseModel.getConfiguration(), classifierOptionsDatabaseModels));
    }

    @Test
    void testUpdateBuildInClassifiersConfigurationWithEmptyOptions() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(true);
        assertThrows(IllegalArgumentException.class,
                () -> classifierOptionsService.updateBuildInClassifiersConfiguration(
                        classifierOptionsDatabaseModel.getConfiguration(), Collections.emptySet()));
    }

    @Test
    void testUpdateBuildInClassifiersConfigurationWithEmptyLatestOptions() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(true);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        classifierOptionsService.updateBuildInClassifiersConfiguration(classifiersConfiguration, Sets.newHashSet(
                Collections.singletonList(
                        createClassifierOptionsDatabaseModel(optionsJson, classifiersConfiguration))));
        assertThat(classifierOptionsDatabaseModelRepository.count()).isOne();
        ClassifiersConfiguration actualConfiguration =
                classifiersConfigurationRepository.findById(classifiersConfiguration.getId()).orElse(null);
        assertThat(actualConfiguration).isNotNull();
        assertThat(actualConfiguration.getUpdated()).isNull();
    }

    @Test
    void testUpdateBuildInClassifiersConfigurationWithOtherSize() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(true);
        ClassifierOptionsDatabaseModel newFirst = createClassifierOptionsDatabaseModel("config1",
                classifierOptionsDatabaseModel.getConfiguration());
        ClassifierOptionsDatabaseModel newSecond = createClassifierOptionsDatabaseModel("config2",
                classifierOptionsDatabaseModel.getConfiguration());
        Set<ClassifierOptionsDatabaseModel> newOptions = Sets.newHashSet(newFirst, newSecond);
        classifierOptionsService.updateBuildInClassifiersConfiguration(
                classifierOptionsDatabaseModel.getConfiguration(), newOptions);
        assertThat(classifierOptionsDatabaseModelRepository.count()).isEqualTo(newOptions.size());
        ClassifiersConfiguration actualConfiguration = classifiersConfigurationRepository.findById(
                classifierOptionsDatabaseModel.getConfiguration().getId()).orElse(null);
        assertThat(actualConfiguration).isNotNull();
        assertThat(actualConfiguration.getUpdated()).isNotNull();
        assertThat(
                classifierOptionsDatabaseModelRepository.existsById(classifierOptionsDatabaseModel.getId())).isFalse();
        assertThat(classifierOptionsDatabaseModelRepository.existsById(newFirst.getId())).isTrue();
        assertThat(classifierOptionsDatabaseModelRepository.existsById(newSecond.getId())).isTrue();
    }

    @Test
    void testUpdateBuildInClassifiersConfigurationWithSameSize() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(true);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        //Creates latest options
        ClassifierOptionsDatabaseModel latestFirst =
                createClassifierOptionsDatabaseModel("config1", classifiersConfiguration);
        latestFirst.setOptionsName("config1Name");
        ClassifierOptionsDatabaseModel latestSecond =
                createClassifierOptionsDatabaseModel("config2", classifiersConfiguration);
        latestSecond.setOptionsName("config2Name");
        List<ClassifierOptionsDatabaseModel> latestOptions = Arrays.asList(latestFirst, latestSecond);
        classifierOptionsDatabaseModelRepository.saveAll(latestOptions);
        //Creates new options
        ClassifierOptionsDatabaseModel newFirst =
                createClassifierOptionsDatabaseModel("config3", classifiersConfiguration);
        newFirst.setOptionsName("config1Name");
        ClassifierOptionsDatabaseModel newSecond =
                createClassifierOptionsDatabaseModel("config2", classifiersConfiguration);
        newSecond.setOptionsName("config2Name");
        Set<ClassifierOptionsDatabaseModel> newOptions = Sets.newHashSet(newFirst, newSecond);
        //Performs test
        classifierOptionsService.updateBuildInClassifiersConfiguration(classifiersConfiguration, newOptions);
        assertThat(classifierOptionsDatabaseModelRepository.count()).isEqualTo(newOptions.size());
        ClassifiersConfiguration actualConfiguration =
                classifiersConfigurationRepository.findById(classifiersConfiguration.getId()).orElse(null);
        assertThat(actualConfiguration).isNotNull();
        assertThat(actualConfiguration.getUpdated()).isNotNull();
        assertThat(classifierOptionsDatabaseModelRepository.existsById(latestFirst.getId())).isFalse();
        assertThat(classifierOptionsDatabaseModelRepository.existsById(latestSecond.getId())).isTrue();
        assertThat(classifierOptionsDatabaseModelRepository.existsById(newFirst.getId())).isTrue();
    }

    private void verifyClassifiersConfigurationHistory(ClassifiersConfiguration classifiersConfiguration,
                                                       ClassifiersConfigurationActionType expectedActionType) {
        var classifiersConfigurationHistoryList = classifiersConfigurationHistoryRepository.findAll();
        assertThat(classifiersConfigurationHistoryList).hasSize(1);
        var classifiersConfigurationHistory = classifiersConfigurationHistoryList.iterator().next();
        assertThat(classifiersConfigurationHistory.getConfiguration().getId())
                .isEqualTo(classifiersConfiguration.getId());
        assertThat(classifiersConfigurationHistory.getCreatedBy()).isEqualTo(USER_NAME);
        assertThat(classifiersConfigurationHistory.getActionType()).isEqualTo(expectedActionType);
    }

    private ClassifierOptionsDatabaseModel saveClassifierOptions(boolean buildIn) {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(buildIn);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                createClassifierOptionsDatabaseModel(optionsJson, classifiersConfiguration);
        return classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
    }

}
