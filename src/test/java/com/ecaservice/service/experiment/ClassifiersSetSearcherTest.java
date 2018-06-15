package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.mapping.options.ClassifierOptionsMapper;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import com.ecaservice.model.options.LogisticOptions;
import com.ecaservice.service.evaluation.EvaluationService;
import com.ecaservice.service.experiment.handler.ClassifierInputDataHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.ClassifiersSet;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifiersSetSearcher} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClassifiersSetSearcherTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int CONFIG_VERSION = 1;

    @Inject
    private ExperimentConfig experimentConfig;
    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private List<ClassifierInputDataHandler> classifierInputDataHandlers;
    @Inject
    private List<ClassifierOptionsMapper> classifierOptionsMappers;
    @Mock
    private EvaluationService evaluationService;
    @Mock
    private ExperimentConfigurationService experimentConfigurationService;

    private ClassifiersSetSearcher classifiersSetSearcher;
    private Instances testInstances;
    private EvaluationResults evaluationResults;

    @Before
    public void init() throws Exception {
        testInstances = TestHelperUtils.loadInstances();
        classifiersSetSearcher = new ClassifiersSetSearcher(evaluationService, experimentConfigurationService,
                experimentConfig, crossValidationConfig, classifierInputDataHandlers, classifierOptionsMappers);
        evaluationResults = new EvaluationResults(new KNearestNeighbours(), new Evaluation(testInstances));
    }

    @Test(expected = ExperimentException.class)
    public void testEmptyConfigs() {
        when(experimentConfigurationService.findLastClassifiersOptions()).thenReturn(Collections.emptyList());
        classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod.TRAINING_DATA,
                Collections.emptyMap());
    }

    @Test(expected = ExperimentException.class)
    public void testErrorConfigs() {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel = new ClassifierOptionsDatabaseModel();
        classifierOptionsDatabaseModel.setVersion(CONFIG_VERSION);
        classifierOptionsDatabaseModel.setConfig("config");
        when(experimentConfigurationService.findLastClassifiersOptions()).thenReturn(
                Collections.singletonList(classifierOptionsDatabaseModel));
        classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod.TRAINING_DATA,
                Collections.emptyMap());
    }

    @Test(expected = ExperimentException.class)
    public void testEmptySet() throws Exception {
        ClassificationResult classificationResult = new ClassificationResult();
        ClassifierOptionsDatabaseModel logisticModel = TestHelperUtils.createClassifierOptionsDatabaseModel(
                objectMapper.writeValueAsString(new LogisticOptions()), CONFIG_VERSION);
        ClassifierOptionsDatabaseModel knnModel = TestHelperUtils.createClassifierOptionsDatabaseModel(
                objectMapper.writeValueAsString(new KNearestNeighboursOptions()), CONFIG_VERSION);
        when(experimentConfigurationService.findLastClassifiersOptions()).thenReturn(
                Arrays.asList(logisticModel, knnModel));
        when(evaluationService.evaluateModel(anyObject(), anyObject(), anyObject())).thenReturn(classificationResult);
        classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod.TRAINING_DATA,
                Collections.emptyMap());
    }

    /**
     * Unit test that checking success building cases:
     * Case 1: the classifiers size is greater than best classifiers number specified in configs.
     * Case 2: the classifiers size is less than best classifiers number specified in configs.
     */
    @Test
    public void testSuccessBuilt() throws Exception {
        ClassificationResult classificationResult = new ClassificationResult();
        classificationResult.setSuccess(true);
        classificationResult.setEvaluationResults(evaluationResults);
        //checks case 1
        List<ClassifierOptionsDatabaseModel> optionsList = new ArrayList<>();
        for (DistanceType distanceType : DistanceType.values()) {
            KNearestNeighboursOptions kNearestNeighboursOptions = new KNearestNeighboursOptions();
            kNearestNeighboursOptions.setDistanceType(distanceType);
            optionsList.add(TestHelperUtils.createClassifierOptionsDatabaseModel(
                    objectMapper.writeValueAsString(kNearestNeighboursOptions), CONFIG_VERSION));
        }
        optionsList.add(TestHelperUtils.createClassifierOptionsDatabaseModel(
                objectMapper.writeValueAsString(new LogisticOptions()), CONFIG_VERSION));
        when(experimentConfigurationService.findLastClassifiersOptions()).thenReturn(optionsList);
        when(evaluationService.evaluateModel(anyObject(), anyObject(), anyObject())).thenReturn(classificationResult);
        ClassifiersSet classifiers = classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod
                .TRAINING_DATA, Collections.emptyMap());
        assertThat(classifiers.size()).isEqualTo(experimentConfig.getEnsemble().getNumBestClassifiers().intValue());
        //checks case 2
        optionsList = Arrays.asList(
                TestHelperUtils.createClassifierOptionsDatabaseModel(
                        objectMapper.writeValueAsString(new LogisticOptions()), CONFIG_VERSION),
                TestHelperUtils.createClassifierOptionsDatabaseModel(
                        objectMapper.writeValueAsString(new KNearestNeighboursOptions()), CONFIG_VERSION));
        when(experimentConfigurationService.findLastClassifiersOptions()).thenReturn(optionsList);
        classifiers = classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod
                .TRAINING_DATA, Collections.emptyMap());
        assertThat(classifiers.size()).isEqualTo(optionsList.size());
    }


}
