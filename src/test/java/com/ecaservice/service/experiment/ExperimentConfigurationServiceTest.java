package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.mapping.ClassifierOptionsMapper;
import com.ecaservice.mapping.DecisionTreeFactory;
import com.ecaservice.mapping.DecisionTreeOptionsMapperImpl;
import com.ecaservice.mapping.KNearestNeighboursOptionsMapperImpl;
import com.ecaservice.mapping.LogisticOptionsMapperImpl;
import com.ecaservice.mapping.NeuralNetworkOptionsMapperImpl;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.options.ActivationFunctionOptions;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.model.options.DecisionTreeOptions;
import com.ecaservice.model.options.LogisticOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.ensemble.forests.DecisionTreeType;
import eca.neural.functions.ActivationFunctionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import weka.classifiers.AbstractClassifier;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ExperimentConfigurationService} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({ExperimentConfig.class, DecisionTreeOptionsMapperImpl.class, LogisticOptionsMapperImpl.class,
        KNearestNeighboursOptionsMapperImpl.class, NeuralNetworkOptionsMapperImpl.class, DecisionTreeFactory.class,
        ExperimentConfigurationService.class})
public class ExperimentConfigurationServiceTest extends AbstractExperimentTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int CONFIG_VERSION = 1;

    @Autowired
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;
    @Autowired
    private ExperimentConfig experimentConfig;
    @Autowired
    private List<ClassifierOptionsMapper> classifierOptionsMappers;

    private ExperimentConfigurationService experimentConfigurationService;

    @Before
    public void init() {
        experimentConfigurationService =
                new ExperimentConfigurationService(experimentConfig, classifierOptionsDatabaseModelRepository,
                        classifierOptionsMappers);
        classifierOptionsDatabaseModelRepository.deleteAll();
    }

    @Test(expected = ExperimentException.class)
    public void testEmptyClassifiersList() {
        experimentConfigurationService.findClassifiers();
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
        List<AbstractClassifier> classifierList = experimentConfigurationService.findClassifiers();
        assertThat(classifierList.size()).isEqualTo(3);
    }

    @Test(expected = ExperimentException.class)
    public void testReadingConfigsWithError() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                TestHelperUtils.createClassifierOptionsDatabaseModel(null,
                        CONFIG_VERSION);
        classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
        experimentConfigurationService.findClassifiers();
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
    public void testSaveSameConfigs() throws IOException {
        URL modelsUrl = getClass().getClassLoader().getResource(experimentConfig.getIndividualClassifiersStoragePath());
        File classifiersOptionsDir = new File(modelsUrl.getPath());
        File[] modelFiles = classifiersOptionsDir.listFiles();
        for (File modelFile : modelFiles) {
            ClassifierOptions classifierOptions = objectMapper.readValue(modelFile, ClassifierOptions.class);
            experimentConfigurationService.saveClassifierOptions(classifierOptions);
        }
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsDatabaseModelRepository.findAll();
        assertThat(classifierOptionsDatabaseModels.size()).isEqualTo(modelFiles.length);
        experimentConfigurationService.saveClassifiersOptions();
        classifierOptionsDatabaseModels = classifierOptionsDatabaseModelRepository.findAll();
        assertThat(classifierOptionsDatabaseModels.size()).isEqualTo(modelFiles.length);
    }
}
