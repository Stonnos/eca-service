package com.ecaservice.service.experiment;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ClassifierOptionsException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.classifiers.ClassifierOptionsService;
import org.junit.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ExperimentConfigurationService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, CommonConfig.class, ClassifierOptionsService.class})
public class ExperimentConfigurationServiceTest extends AbstractJpaTest {

    @Inject
    private ClassifierOptionsService classifierOptionsService;
    @Inject
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Inject
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Inject
    private ExperimentConfig experimentConfig;

    private ExperimentConfigurationService experimentConfigurationService;

    @Override
    public void init() {
        experimentConfigurationService = new ExperimentConfigurationService(classifierOptionsService, experimentConfig,
                classifiersConfigurationRepository);
    }

    @Override
    public void deleteAll() {
        classifierOptionsDatabaseModelRepository.deleteAll();
        classifiersConfigurationRepository.deleteAll();
    }

    @Test(expected = ClassifierOptionsException.class)
    public void testNotSpecifiedConfigsDirectory() {
        ExperimentConfig config = new ExperimentConfig();
        ReflectionTestUtils.setField(experimentConfigurationService, "experimentConfig", config);
        experimentConfigurationService.saveClassifiersOptions();
    }

    @Test
    public void testSaveNewConfigs() {
        URL modelsUrl = getClass().getClassLoader().getResource(experimentConfig.getIndividualClassifiersStoragePath());
        File classifiersOptionsDir = new File(modelsUrl.getPath());
        File[] modelFiles = classifiersOptionsDir.listFiles();
        experimentConfigurationService.saveClassifiersOptions();
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAll();
        assertThat(classifiersConfigurationRepository.count()).isOne();
        assertThat(classifierOptionsDatabaseModels.size()).isEqualTo(modelFiles.length);
    }

    @Test
    public void testSaveSameConfigs() {
        URL modelsUrl = getClass().getClassLoader().getResource(experimentConfig.getIndividualClassifiersStoragePath());
        File classifiersOptionsDir = new File(modelsUrl.getPath());
        File[] modelFiles = classifiersOptionsDir.listFiles();
        experimentConfigurationService.saveClassifiersOptions();
        experimentConfigurationService.saveClassifiersOptions();
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAll();
        assertThat(classifierOptionsDatabaseModels.size()).isEqualTo(modelFiles.length);
        assertThat(classifiersConfigurationRepository.count()).isOne();
    }
}
