package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.model.evaluation.ClassificationResult;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.service.evaluation.EvaluationService;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.ClassifiersSet;
import eca.metrics.KNearestNeighbours;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifiersSetSearcher} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(ExperimentConfig.class)
public class ClassifiersSetSearcherTest extends AbstractExperimentTest {

    @Autowired
    private ExperimentConfig experimentConfig;
    @Mock
    private EvaluationService evaluationService;

    private ClassifiersSetSearcher classifiersSetSearcher;
    private Instances testInstances;
    private EvaluationResults evaluationResults;

    @Before
    public void init() throws Exception {
        testInstances = TestHelperUtils.loadInstances();
        classifiersSetSearcher = new ClassifiersSetSearcher(evaluationService, experimentConfig);
        evaluationResults = new EvaluationResults(new KNearestNeighbours(), new Evaluation(testInstances));
    }

    @Test(expected = ExperimentException.class)
    public void testEmptySet() {
        ClassificationResult classificationResult = new ClassificationResult();
        when(evaluationService.evaluateModel(anyObject(), anyObject(), anyObject())).thenReturn(classificationResult);
        classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod.TRAINING_DATA,
                Collections.emptyMap());
    }

    @Test
    public void testSuccessBuilt() {
        ClassificationResult classificationResult = new ClassificationResult();
        classificationResult.setSuccess(true);
        classificationResult.setEvaluationResults(evaluationResults);
        when(evaluationService.evaluateModel(anyObject(), anyObject(), anyObject())).thenReturn(classificationResult);
        ClassifiersSet classifiers = classifiersSetSearcher.findBestClassifiers(testInstances, EvaluationMethod
                .TRAINING_DATA, Collections.emptyMap());
        assertEquals(classifiers.size(), experimentConfig.getEnsemble().getNumBestClassifiers().intValue());
    }


}
