package com.ecaservice.server.service.experiment;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.message.template.service.MessageTemplateProcessor;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.exception.ClassifierOptionsException;
import com.ecaservice.server.mapping.ClassifierOptionsDatabaseModelMapperImpl;
import com.ecaservice.server.mapping.ClassifiersConfigurationHistoryMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.classifiers.ClassifierOptionsInfoProcessor;
import com.ecaservice.server.service.classifiers.ClassifierOptionsService;
import com.ecaservice.server.service.classifiers.ClassifiersConfigurationHistoryService;
import com.ecaservice.server.service.classifiers.ClassifiersFormTemplateProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link ExperimentConfigurationService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, AppProperties.class, ClassifierOptionsService.class,
        ClassifierOptionsDatabaseModelMapperImpl.class, DateTimeConverter.class,
        ClassifiersConfigurationHistoryService.class, ClassifiersConfigurationHistoryMapperImpl.class})
class ExperimentConfigurationServiceTest extends AbstractJpaTest {

    @MockBean
    private UserService userService;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    @MockBean
    private MessageTemplateProcessor messageTemplateProcessor;
    @MockBean
    private ClassifiersFormTemplateProvider classifiersFormTemplateProvider;
    @Autowired
    private ClassifierOptionsService classifierOptionsService;
    @Autowired
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Autowired
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Autowired
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
        assertThat(classifierOptionsDatabaseModels).hasSize(modelFiles.length);
    }

    @Test
    void testSaveSameConfigs() throws IOException {
        Resource[] modelFiles = resolver.getResources(experimentConfig.getIndividualClassifiersStoragePath());
        experimentConfigurationService.saveClassifiersOptions();
        experimentConfigurationService.saveClassifiersOptions();
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAll();
        assertThat(classifierOptionsDatabaseModels).hasSize(modelFiles.length);
        assertThat(classifiersConfigurationRepository.count()).isOne();
    }
}
