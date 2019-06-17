package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_;
import com.ecaservice.model.options.ActivationFunctionOptions;
import com.ecaservice.model.options.DecisionTreeOptions;
import com.ecaservice.model.options.LogisticOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.ensemble.forests.DecisionTreeType;
import eca.neural.functions.ActivationFunctionType;
import org.junit.Test;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ExperimentConfigurationService} functionality.
 *
 * @author Roman Batygin
 */
@Import(ExperimentConfig.class)
public class ExperimentConfigurationServiceTest extends AbstractJpaTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int CONFIG_VERSION = 1;

    @Inject
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Inject
    private ExperimentConfig experimentConfig;

    private ExperimentConfigurationService experimentConfigurationService;

    @Override
    public void init() {
        experimentConfigurationService =
                new ExperimentConfigurationService(experimentConfig, classifierOptionsDatabaseModelRepository);
    }

    @Override
    public void deleteAll() {
        classifierOptionsDatabaseModelRepository.deleteAll();
    }

    @Test
    public void testSuccessReadingConfigs() throws IOException {
        LogisticOptions logisticOptions = new LogisticOptions();
        logisticOptions.setMaxIts(500);
        logisticOptions.setUseConjugateGradientDescent(false);
        NeuralNetworkOptions neuralNetworkOptions = new NeuralNetworkOptions();
        neuralNetworkOptions.setActivationFunctionOptions(new ActivationFunctionOptions());
        neuralNetworkOptions.getActivationFunctionOptions().setActivationFunctionType(
                ActivationFunctionType.EXPONENTIAL);
        DecisionTreeOptions decisionTreeOptions = new DecisionTreeOptions();
        decisionTreeOptions.setRandomTree(true);
        decisionTreeOptions.setNumRandomAttr(10);
        decisionTreeOptions.setDecisionTreeType(DecisionTreeType.C45);
        decisionTreeOptions.setMaxDepth(25);
        //Saves classifiers options
        ClassifierOptionsDatabaseModel logisticOptionsDatabaseModel =
                TestHelperUtils.createClassifierOptionsDatabaseModel(objectMapper.writeValueAsString(logisticOptions),
                        CONFIG_VERSION);
        classifierOptionsDatabaseModelRepository.save(logisticOptionsDatabaseModel);
        ClassifierOptionsDatabaseModel neuralNetworkOptionsDatabaseModel =
                TestHelperUtils.createClassifierOptionsDatabaseModel(
                        objectMapper.writeValueAsString(neuralNetworkOptions), CONFIG_VERSION);
        classifierOptionsDatabaseModelRepository.save(neuralNetworkOptionsDatabaseModel);
        ClassifierOptionsDatabaseModel treeOptionsDatabaseModel = TestHelperUtils.createClassifierOptionsDatabaseModel(
                objectMapper.writeValueAsString(decisionTreeOptions), CONFIG_VERSION);
        classifierOptionsDatabaseModelRepository.save(treeOptionsDatabaseModel);
        List<ClassifierOptionsDatabaseModel> classifierList =
                experimentConfigurationService.findLastClassifiersOptions();
        assertThat(classifierList.size()).isEqualTo(3);
    }

    @Test(expected = ExperimentException.class)
    public void testNotSpecifiedConfigsDirectory() {
        ExperimentConfig experimentConfig = new ExperimentConfig();
        ReflectionTestUtils.setField(experimentConfigurationService, "experimentConfig", experimentConfig);
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
    }

    @Test
    public void testGetConfigsPage() {
        URL modelsUrl = getClass().getClassLoader().getResource(experimentConfig.getIndividualClassifiersStoragePath());
        File classifiersOptionsDir = new File(modelsUrl.getPath());
        File[] modelFiles = classifiersOptionsDir.listFiles();
        experimentConfigurationService.saveClassifiersOptions();
        PageRequestDto pageRequestDto =
                new PageRequestDto(0, modelFiles.length / 2, ClassifierOptionsDatabaseModel_.CREATION_DATE, false, null,
                        Collections.emptyList());
        Page<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModelPage =
                experimentConfigurationService.getNextPage(pageRequestDto);
        assertThat(classifierOptionsDatabaseModelPage).isNotNull();
        assertThat(classifierOptionsDatabaseModelPage.getTotalElements()).isEqualTo(modelFiles.length);
        assertThat(classifierOptionsDatabaseModelPage.getContent().size()).isEqualTo(pageRequestDto.getSize());
    }
}
