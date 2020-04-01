package com.ecaservice.service.classifiers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.exception.EntityNotFoundException;
import com.ecaservice.mapping.ClassifiersConfigurationMapperImpl;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.entity.ClassifiersConfigurationSource;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import org.junit.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassifiersConfigurationService} functionality
 *
 * @author Roman Batygin
 */
@Import({ClassifiersConfigurationService.class, ClassifiersConfigurationMapperImpl.class})
public class ClassifiersConfigurationServiceTest extends AbstractJpaTest {

    private static final String TEST_CONFIG = "test_config";
    private static final String TEST_CONFIGURATION_NAME = "TestConfiguration";
    private static final String TEST_CONFIGURATION_UPDATED_NAME = "UpdatedTestName";
    private static final long ID = 1L;

    @Inject
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Inject
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Inject
    private ClassifiersConfigurationService classifiersConfigurationService;

    @Override
    public void deleteAll() {
        classifierOptionsDatabaseModelRepository.deleteAll();
        classifiersConfigurationRepository.deleteAll();
    }

    @Test
    public void testSaveClassifiersConfiguration() {
        CreateClassifiersConfigurationDto configurationDto = new CreateClassifiersConfigurationDto();
        configurationDto.setName(TEST_CONFIGURATION_NAME);
        classifiersConfigurationService.save(configurationDto);
        List<ClassifiersConfiguration> configurations = classifiersConfigurationRepository.findAll();
        assertThat(configurations).hasSize(1);
        ClassifiersConfiguration actual = configurations.iterator().next();
        assertThat(actual.getName()).isEqualTo(configurationDto.getName());
        assertThat(actual.getCreated()).isNotNull();
        assertThat(actual.getSource()).isEqualTo(ClassifiersConfigurationSource.MANUAL);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdateNotExistingConfiguration() {
        UpdateClassifiersConfigurationDto updateClassifiersConfigurationDto = new UpdateClassifiersConfigurationDto();
        updateClassifiersConfigurationDto.setId(ID);
        classifiersConfigurationService.update(updateClassifiersConfigurationDto);
    }

    @Test
    public void testUpdateClassifiersConfiguration() {
        ClassifiersConfiguration classifiersConfiguration =
                saveConfiguration(true, ClassifiersConfigurationSource.MANUAL);
        assertThat(classifiersConfiguration.getUpdated()).isNull();
        assertThat(classifiersConfiguration.getName()).isEqualTo(TEST_CONFIGURATION_NAME);
        UpdateClassifiersConfigurationDto updateClassifiersConfigurationDto = new UpdateClassifiersConfigurationDto();
        updateClassifiersConfigurationDto.setName(TEST_CONFIGURATION_UPDATED_NAME);
        updateClassifiersConfigurationDto.setId(classifiersConfiguration.getId());
        classifiersConfigurationService.update(updateClassifiersConfigurationDto);
        List<ClassifiersConfiguration> configurations = classifiersConfigurationRepository.findAll();
        assertThat(configurations).hasSize(1);
        ClassifiersConfiguration actual = configurations.iterator().next();
        assertThat(actual.getName()).isEqualTo(updateClassifiersConfigurationDto.getName());
        assertThat(actual.getUpdated()).isNotNull();
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteNotExistingConfiguration() {
        classifiersConfigurationService.delete(ID);
    }

    @Test
    public void testDeleteConfiguration() {
        ClassifiersConfiguration classifiersConfiguration =
                saveConfiguration(false, ClassifiersConfigurationSource.MANUAL);
        classifiersConfigurationService.delete(classifiersConfiguration.getId());
        ClassifiersConfiguration actualConfiguration =
                classifiersConfigurationRepository.findById(classifiersConfiguration.getId()).orElse(null);
        assertThat(actualConfiguration).isNull();
        assertThat(classifierOptionsDatabaseModelRepository.count()).isZero();
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteActiveConfiguration() {
        ClassifiersConfiguration classifiersConfiguration =
                saveConfiguration(true, ClassifiersConfigurationSource.MANUAL);
        classifiersConfigurationService.delete(classifiersConfiguration.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteSystemConfiguration() {
        ClassifiersConfiguration classifiersConfiguration =
                saveConfiguration(true, ClassifiersConfigurationSource.SYSTEM);
        classifiersConfigurationService.delete(classifiersConfiguration.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testSetActiveNotExistingConfiguration() {
        classifiersConfigurationService.setActive(ID);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetActiveNotExistingActiveConfiguration() {
        ClassifiersConfiguration classifiersConfiguration =
                saveConfiguration(false, ClassifiersConfigurationSource.MANUAL);
        classifiersConfigurationService.setActive(classifiersConfiguration.getId());
    }

    @Test
    public void testSetActiveConfiguration() {
        ClassifiersConfiguration lastActive = saveConfiguration(true, ClassifiersConfigurationSource.MANUAL);
        ClassifiersConfiguration newActive = saveConfiguration(false, ClassifiersConfigurationSource.MANUAL);
        classifiersConfigurationService.setActive(newActive.getId());
        ClassifiersConfiguration actualActive =
                classifiersConfigurationRepository.findById(newActive.getId()).orElse(null);
        ClassifiersConfiguration actualNotActive =
                classifiersConfigurationRepository.findById(lastActive.getId()).orElse(null);
        assertThat(actualActive).isNotNull();
        assertThat(actualActive.isActive()).isTrue();
        assertThat(actualNotActive).isNotNull();
        assertThat(actualNotActive.isActive()).isNotNull();
    }

    private ClassifiersConfiguration saveConfiguration(boolean active, ClassifiersConfigurationSource source) {
        ClassifiersConfiguration classifiersConfiguration = TestHelperUtils.createClassifiersConfiguration();
        classifiersConfiguration.setSource(source);
        classifiersConfiguration.setActive(active);
        classifiersConfiguration.setName(TEST_CONFIGURATION_NAME);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        classifierOptionsDatabaseModelRepository.saveAll(Arrays.asList(
                TestHelperUtils.createClassifierOptionsDatabaseModel(TEST_CONFIG, classifiersConfiguration),
                TestHelperUtils.createClassifierOptionsDatabaseModel(TEST_CONFIG, classifiersConfiguration)
        ));
        return classifiersConfiguration;
    }
}
