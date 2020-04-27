package com.ecaservice.service.classifiers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.exception.EntityNotFoundException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.options.AdaBoostOptions;
import com.ecaservice.model.options.LogisticOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.TestHelperUtils.createClassifierOptionsDatabaseModel;
import static com.ecaservice.TestHelperUtils.createClassifiersConfiguration;
import static com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_.CREATION_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link ClassifierOptionsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({CommonConfig.class, ClassifierOptionsService.class})
public class ClassifierOptionsServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String OPTIONS = "options";
    private static final long ID = 1L;

    @Inject
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Inject
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Inject
    private ClassifierOptionsService classifierOptionsService;

    @Override
    public void deleteAll() {
        classifierOptionsDatabaseModelRepository.deleteAll();
        classifiersConfigurationRepository.deleteAll();
    }

    @Test
    public void testGetClassifiersOptionsPage() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(true);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, CREATION_DATE, false, null,
                        Collections.emptyList());
        Page<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModelPage =
                classifierOptionsService.getNextPage(classifierOptionsDatabaseModel.getConfiguration().getId(),
                        pageRequestDto);
        assertThat(classifierOptionsDatabaseModelPage).isNotNull();
        assertThat(classifierOptionsDatabaseModelPage.getTotalElements()).isOne();
    }

    @Test
    public void testGetClassifiersOptionsPageForNotExistingConfiguration() {
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, CREATION_DATE, false, null,
                        Collections.emptyList());
        assertThrows(EntityNotFoundException.class, () -> classifierOptionsService.getNextPage(ID, pageRequestDto));
    }

    @Test
    public void testGetActiveOptionsForNotExistsConfiguration() {
        assertThrows(IllegalStateException.class, () -> classifierOptionsService.getActiveClassifiersOptions());
    }

    @Test
    public void testGetActiveOptions() {
        saveClassifierOptions(true);
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsService.getActiveClassifiersOptions();
        assertThat(classifierOptionsDatabaseModels).hasSize(1);
    }

    @Test
    public void testDeleteNotExistingOptions() {
        assertThrows(EntityNotFoundException.class, () -> classifierOptionsService.deleteOptions(ID));
    }

    @Test
    public void testDeleteOptionsForActiveConfiguration() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(false);
        classifierOptionsDatabaseModelRepository.save(
                createClassifierOptionsDatabaseModel(OPTIONS, classifierOptionsDatabaseModel.getConfiguration()));
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
    public void testDeleteOptionsForNotActiveConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setActive(false);
        classifiersConfiguration.setBuildIn(false);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = classifierOptionsDatabaseModelRepository.save(
                createClassifierOptionsDatabaseModel(OPTIONS, classifiersConfiguration));
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
    public void testDeleteOptionsForActiveConfigurationWithSingleOption() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setActive(true);
        classifiersConfiguration.setBuildIn(false);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                createClassifierOptionsDatabaseModel(OPTIONS, classifiersConfiguration);
        classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
        assertThrows(IllegalStateException.class,
                () -> classifierOptionsService.deleteOptions(classifierOptionsDatabaseModel.getId()));
    }

    @Test
    public void testDeleteFromBuildInConfiguration() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(true);
        assertThrows(IllegalStateException.class,
                () -> classifierOptionsService.deleteOptions(classifierOptionsDatabaseModel.getId()));
    }

    @Test
    public void testSaveOptionsForNotExistingClassifiersConfiguration() {
        assertThrows(EntityNotFoundException.class,
                () -> classifierOptionsService.saveClassifierOptions(ID, TestHelperUtils.createLogisticOptions()));
    }

    @Test
    public void testSaveClassifierOptions() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(false);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        LogisticOptions logisticOptions = TestHelperUtils.createLogisticOptions();
        ClassifierOptionsDatabaseModel saved =
                classifierOptionsService.saveClassifierOptions(classifiersConfiguration.getId(), logisticOptions);
        ClassifierOptionsDatabaseModel actual =
                classifierOptionsDatabaseModelRepository.findById(saved.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getOptionsName()).isEqualTo(logisticOptions.getClass().getSimpleName());
        assertThat(actual.getConfigMd5Hash()).isNotNull();
        assertThat(actual.getConfig()).isNotNull();
        assertThat(actual.getCreationDate()).isNotNull();
        assertThat(actual.getConfiguration().getUpdated()).isNotNull();
    }

    @Test
    public void testSaveClassifierOptionsToBuildInConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(true);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        LogisticOptions logisticOptions = TestHelperUtils.createLogisticOptions();
        assertThrows(IllegalStateException.class,
                () -> classifierOptionsService.saveClassifierOptions(classifiersConfiguration.getId(),
                        logisticOptions));
    }

    @Test
    public void testSaveEnsembleClassifierOptions() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(false);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        AdaBoostOptions adaBoostOptions = TestHelperUtils.createAdaBoostOptions();
        assertThrows(IllegalStateException.class,
                () -> classifierOptionsService.saveClassifierOptions(classifiersConfiguration.getId(),
                        adaBoostOptions));
    }

    @Test
    public void testUpdateNotBuildInClassifiersConfigurationOptions() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(false);
        assertThrows(IllegalStateException.class, () -> classifierOptionsService.updateBuildInClassifiersConfiguration(
                classifierOptionsDatabaseModel.getConfiguration(), Collections.singletonList(
                        createClassifierOptionsDatabaseModel(OPTIONS,
                                classifierOptionsDatabaseModel.getConfiguration()))));
    }

    @Test
    public void testUpdateBuildInClassifiersConfigurationWithEmptyOptions() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(true);
        assertThrows(IllegalArgumentException.class, () -> classifierOptionsService.updateBuildInClassifiersConfiguration(
                classifierOptionsDatabaseModel.getConfiguration(), Collections.emptyList()));
    }

    @Test
    public void testUpdateBuildInClassifiersConfigurationWithEmptyLatestOptions() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(true);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        classifierOptionsService.updateBuildInClassifiersConfiguration(classifiersConfiguration,
                Collections.singletonList(createClassifierOptionsDatabaseModel(OPTIONS, classifiersConfiguration)));
        assertThat(classifierOptionsDatabaseModelRepository.count()).isOne();
        ClassifiersConfiguration actualConfiguration =
                classifiersConfigurationRepository.findById(classifiersConfiguration.getId()).orElse(null);
        assertThat(actualConfiguration).isNotNull();
        assertThat(actualConfiguration.getUpdated()).isNull();
    }

    @Test
    public void testUpdateBuildInClassifiersConfigurationWithOtherSize() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(true);
        ClassifierOptionsDatabaseModel newFirst = createClassifierOptionsDatabaseModel("config1",
                classifierOptionsDatabaseModel.getConfiguration());
        ClassifierOptionsDatabaseModel newSecond = createClassifierOptionsDatabaseModel("config2",
                classifierOptionsDatabaseModel.getConfiguration());
        List<ClassifierOptionsDatabaseModel> newOptions = Arrays.asList(newFirst, newSecond);
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
    public void testUpdateBuildInClassifiersConfigurationWithSameSize() {
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
        List<ClassifierOptionsDatabaseModel> newOptions = Arrays.asList(newFirst, newSecond);
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

    private ClassifierOptionsDatabaseModel saveClassifierOptions(boolean buildIn) {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(buildIn);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                createClassifierOptionsDatabaseModel(OPTIONS, classifiersConfiguration);
        return classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
    }

}
