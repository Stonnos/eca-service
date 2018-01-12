package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.mapping.DecisionTreeFactory;
import com.ecaservice.mapping.DecisionTreeOptionsMapperImpl;
import com.ecaservice.mapping.KNearestNeighboursOptionsMapperImpl;
import com.ecaservice.mapping.LogisticOptionsMapperImpl;
import com.ecaservice.mapping.NeuralNetworkOptionsMapperImpl;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.options.ActivationFunctionOptions;
import com.ecaservice.model.options.LogisticOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.neural.functions.ActivationFunctionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import weka.classifiers.AbstractClassifier;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ExperimentConfigurationService} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import( {ExperimentConfig.class, DecisionTreeOptionsMapperImpl.class, LogisticOptionsMapperImpl.class,
        KNearestNeighboursOptionsMapperImpl.class, NeuralNetworkOptionsMapperImpl.class, DecisionTreeFactory.class,
        ExperimentConfigurationService.class})
public class ExperimentConfigurationServiceTest extends AbstractExperimentTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int CONFIG_VERSION = 1;

    @Autowired
    private ExperimentConfigurationService experimentConfigurationService;
    @Autowired
    private ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    @Before
    public void init() {
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

        ClassifierOptionsDatabaseModel logisticOptionsDatabaseModel =
                TestHelperUtils.createClassifierOptionsDatabaseModel(objectMapper.writeValueAsString(logisticOptions),
                        CONFIG_VERSION);
        classifierOptionsDatabaseModelRepository.save(logisticOptionsDatabaseModel);
        ClassifierOptionsDatabaseModel neuralNetworkOptionsDatabaseModel =
                TestHelperUtils.createClassifierOptionsDatabaseModel(
                        objectMapper.writeValueAsString(neuralNetworkOptions), CONFIG_VERSION);
        classifierOptionsDatabaseModelRepository.save(neuralNetworkOptionsDatabaseModel);

        List<AbstractClassifier> classifierList = experimentConfigurationService.findClassifiers();
        assertThat(classifierList.size()).isEqualTo(2);
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

    @Test(expected = ExperimentException.class)
    public void testInvalidConfigsDirectory() {
        ExperimentConfig experimentConfig = new ExperimentConfig();
        experimentConfig.setIndividualClassifiersStoragePath("/dir");
        ReflectionTestUtils.setField(experimentConfigurationService, "experimentConfig", experimentConfig);
        experimentConfigurationService.saveClassifiersOptions();
    }
}
