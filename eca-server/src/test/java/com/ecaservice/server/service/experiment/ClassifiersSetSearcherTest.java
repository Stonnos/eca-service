package com.ecaservice.server.service.experiment;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.classifier.options.model.KNearestNeighboursOptions;
import com.ecaservice.classifier.options.model.LogisticOptions;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.evaluation.EvaluationInputDataModel;
import com.ecaservice.server.service.classifiers.ClassifierOptionsService;
import com.ecaservice.server.service.evaluation.EvaluationService;
import com.ecaservice.server.service.experiment.handler.ClassifierInputDataHandler;
import com.ecaservice.server.service.experiment.handler.DecisionTreeInputDataHandler;
import com.ecaservice.server.service.experiment.handler.NeuralNetworkInputDataHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.ClassifiersSet;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.core.Instances;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifiersSetSearcher} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ExperimentConfig.class, CrossValidationConfig.class,
        DecisionTreeInputDataHandler.class, NeuralNetworkInputDataHandler.class, ClassifiersOptionsAutoConfiguration.class})
class ClassifiersSetSearcherTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String OPTIONS = "options";

    @Autowired
    private ExperimentConfig experimentConfig;
    @Autowired
    private CrossValidationConfig crossValidationConfig;
    @Autowired
    private List<ClassifierInputDataHandler> classifierInputDataHandlers;
    @Autowired
    private ClassifierOptionsAdapter classifierOptionsAdapter;
    @Mock
    private EvaluationService evaluationService;
    @Mock
    private ClassifierOptionsService classifierOptionsService;

    private ClassifiersSetSearcher classifiersSetSearcher;
    private Instances testInstances;
    private EvaluationResults evaluationResults;

    @BeforeEach
    void init() throws Exception {
        testInstances = TestHelperUtils.loadInstances();
        classifiersSetSearcher =
                new ClassifiersSetSearcher(evaluationService, classifierOptionsService, experimentConfig,
                        crossValidationConfig, classifierInputDataHandlers, classifierOptionsAdapter);
        evaluationResults = new EvaluationResults(new KNearestNeighbours(), new Evaluation(testInstances));
    }

    @Test
    void testEmptyConfigs() {
        when(classifierOptionsService.getActiveClassifiersOptions()).thenReturn(Collections.emptyList());
        assertThrows(ExperimentException.class,
                () -> classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod.TRAINING_DATA));
    }

    @Test
    void testErrorConfigs() {
        ClassifiersConfiguration classifiersConfiguration = TestHelperUtils.createClassifiersConfiguration();
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                TestHelperUtils.createClassifierOptionsDatabaseModel(OPTIONS, classifiersConfiguration);
        when(classifierOptionsService.getActiveClassifiersOptions()).thenReturn(
                Collections.singletonList(classifierOptionsDatabaseModel));
        assertThrows(ExperimentException.class,
                () -> classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod.TRAINING_DATA));
    }

    /**
     * Unit test that checking success building cases:
     * Case 1: the classifiers size is greater than best classifiers number specified in configs.
     * Case 2: the classifiers size is less than best classifiers number specified in configs.
     */
    @Test
    void testSuccessBuilt() throws Exception {
        ClassifiersConfiguration classifiersConfiguration = TestHelperUtils.createClassifiersConfiguration();
        //checks case 1
        List<ClassifierOptionsDatabaseModel> optionsList = newArrayList();
        for (DistanceType distanceType : DistanceType.values()) {
            KNearestNeighboursOptions kNearestNeighboursOptions = new KNearestNeighboursOptions();
            kNearestNeighboursOptions.setDistanceType(distanceType);
            optionsList.add(TestHelperUtils.createClassifierOptionsDatabaseModel(
                    objectMapper.writeValueAsString(kNearestNeighboursOptions), classifiersConfiguration));
        }
        optionsList.add(TestHelperUtils.createClassifierOptionsDatabaseModel(
                objectMapper.writeValueAsString(new LogisticOptions()), classifiersConfiguration));
        when(classifierOptionsService.getActiveClassifiersOptions()).thenReturn(optionsList);
        when(evaluationService.evaluateModel(any(EvaluationInputDataModel.class))).thenReturn(evaluationResults);
        ClassifiersSet classifiers =
                classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod.TRAINING_DATA);
        assertThat(classifiers.size()).isEqualTo(experimentConfig.getEnsemble().getNumBestClassifiers().intValue());
        //checks case 2
        optionsList = Arrays.asList(
                TestHelperUtils.createClassifierOptionsDatabaseModel(
                        objectMapper.writeValueAsString(new LogisticOptions()), classifiersConfiguration),
                TestHelperUtils.createClassifierOptionsDatabaseModel(
                        objectMapper.writeValueAsString(new KNearestNeighboursOptions()), classifiersConfiguration));
        when(classifierOptionsService.getActiveClassifiersOptions()).thenReturn(optionsList);
        classifiers = classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod.TRAINING_DATA);
        assertThat(classifiers.size()).isEqualTo(optionsList.size());
    }
}
