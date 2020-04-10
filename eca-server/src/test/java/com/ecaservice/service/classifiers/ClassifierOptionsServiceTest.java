package com.ecaservice.service.classifiers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.exception.EntityNotFoundException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.options.AdaBoostOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.Test;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.TestHelperUtils.createClassifierOptionsDatabaseModel;
import static com.ecaservice.TestHelperUtils.createClassifiersConfiguration;
import static com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_.CREATION_DATE;
import static org.assertj.core.api.Assertions.assertThat;

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
    public void testGetConfigsPage() {
        saveClassifierOptions(true);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, CREATION_DATE, false, null,
                        Collections.emptyList());
        Page<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModelPage =
                classifierOptionsService.getNextPage(pageRequestDto);
        assertThat(classifierOptionsDatabaseModelPage).isNotNull();
        assertThat(classifierOptionsDatabaseModelPage.getTotalElements()).isOne();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetActiveOptionsForNotExistsConfiguration() {
        classifierOptionsService.getActiveClassifiersOptions();
    }

    @Test
    public void testGetActiveOptions() {
        saveClassifierOptions(true);
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsService.getActiveClassifiersOptions();
        assertThat(classifierOptionsDatabaseModels).hasSize(1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteNotExistingOptions() {
        classifierOptionsService.deleteOptions(ID);
    }

    @Test
    public void testDeleteOptions() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(false);
        classifierOptionsService.deleteOptions(classifierOptionsDatabaseModel.getId());
        ClassifierOptionsDatabaseModel actualOptions =
                classifierOptionsDatabaseModelRepository.findById(classifierOptionsDatabaseModel.getId()).orElse(null);
        assertThat(actualOptions).isNull();
        ClassifiersConfiguration actualConfiguration = classifiersConfigurationRepository.findById(
                classifierOptionsDatabaseModel.getConfiguration().getId()).orElse(null);
        assertThat(actualConfiguration).isNotNull();
        assertThat(actualConfiguration.getUpdated()).isNotNull();
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteFromBuildInConfiguration() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = saveClassifierOptions(true);
        classifierOptionsService.deleteOptions(classifierOptionsDatabaseModel.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testSaveOptionsForNotExistingClassifiersConfiguration() {
        classifierOptionsService.saveClassifierOptions(ID, TestHelperUtils.createAdaBoostOptions());
    }

    @Test
    public void testSaveClassifierOptions() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(false);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        AdaBoostOptions adaBoostOptions = TestHelperUtils.createAdaBoostOptions();
        classifierOptionsService.saveClassifierOptions(classifiersConfiguration.getId(), adaBoostOptions);
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAllByConfiguration(classifiersConfiguration);
        assertThat(classifierOptionsDatabaseModels).hasSize(1);
        ClassifierOptionsDatabaseModel actual = classifierOptionsDatabaseModels.iterator().next();
        assertThat(actual.getOptionsName()).isEqualTo(adaBoostOptions.getClass().getSimpleName());
        assertThat(actual.getConfigMd5Hash()).isNotNull();
        assertThat(actual.getConfig()).isNotNull();
        assertThat(actual.getCreationDate()).isNotNull();
        assertThat(actual.getConfiguration().getUpdated()).isNotNull();
    }

    @Test(expected = IllegalStateException.class)
    public void testSaveClassifierOptionsToBuildInConfiguration() {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        classifiersConfiguration.setBuildIn(true);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        AdaBoostOptions adaBoostOptions = TestHelperUtils.createAdaBoostOptions();
        classifierOptionsService.saveClassifierOptions(classifiersConfiguration.getId(), adaBoostOptions);
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
