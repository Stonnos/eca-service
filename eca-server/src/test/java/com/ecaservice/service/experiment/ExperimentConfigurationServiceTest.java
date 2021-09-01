package com.ecaservice.service.experiment;

import com.ecaservice.config.AppProperties;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ClassifierOptionsException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.UserService;
import com.ecaservice.service.classifiers.ClassifierOptionsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link ExperimentConfigurationService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, AppProperties.class, ClassifierOptionsService.class})
class ExperimentConfigurationServiceTest extends AbstractJpaTest {

    @MockBean
    private UserService userService;
    @Inject
    private ClassifierOptionsService classifierOptionsService;
    @Inject
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Inject
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Inject
    private ExperimentConfig experimentConfig;

    private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    private ExperimentConfigurationService experimentConfigurationService;

    @Override
    public void init() {
        experimentConfigurationService =
                new ExperimentConfigurationService(classifierOptionsService, experimentConfig,
                        classifiersConfigurationRepository);
    }

    @Override
    public void deleteAll() {
        classifierOptionsDatabaseModelRepository.deleteAll();
        classifiersConfigurationRepository.deleteAll();
    }

    @Test
    void testNotSpecifiedConfigsDirectory() {
        ExperimentConfig config = new ExperimentConfig();
        ReflectionTestUtils.setField(experimentConfigurationService, "experimentConfig", config);
        assertThrows(ClassifierOptionsException.class, () -> experimentConfigurationService.saveClassifiersOptions());
    }

    @Test
    void testSaveNewConfigs() throws IOException {
        Resource[] modelFiles = resolver.getResources(experimentConfig.getIndividualClassifiersStoragePath());
        experimentConfigurationService.saveClassifiersOptions();
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAll();
        assertThat(classifiersConfigurationRepository.count()).isOne();
        assertThat(classifierOptionsDatabaseModels.size()).isEqualTo(modelFiles.length);
    }

    @Test
    void testSaveSameConfigs() throws IOException {
        Resource[] modelFiles = resolver.getResources(experimentConfig.getIndividualClassifiersStoragePath());
        experimentConfigurationService.saveClassifiersOptions();
        experimentConfigurationService.saveClassifiersOptions();
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAll();
        assertThat(classifierOptionsDatabaseModels.size()).isEqualTo(modelFiles.length);
        assertThat(classifiersConfigurationRepository.count()).isOne();
    }
}
