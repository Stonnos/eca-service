package com.ecaservice.mapping;

import com.ecaservice.TestDataBuilder;
import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationStatus;
import com.ecaservice.model.entity.InstancesInfo;
import org.junit.Test;
import weka.core.Instances;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Roman Batygin
 */

public class ClassificationResultToEvaluationLogConverterTest extends AbstractConverterTest {

    @Test
    public void testMapClassificationResultToEvaluationLog() throws Exception {
        ClassificationResult result = TestDataBuilder.createClassificationResult(25, 6);
        result.setSuccess(false);
        result.setErrorMessage("message");
        ;

        EvaluationLog evaluationLog = mapper.map(result, EvaluationLog.class);

        assertEquals(evaluationLog.getErrorMessage(), result.getErrorMessage());
        assertEquals(evaluationLog.getEvaluationStatus(), EvaluationStatus.ERROR);
        assertEquals(evaluationLog.getClassifierName(),
                result.getClassifierDescriptor().getClassifier().getClass().getSimpleName());

        InstancesInfo instancesInfo = evaluationLog.getInstancesInfo();

        Instances instances = result.getClassifierDescriptor().getEvaluation().getData();

        assertEquals(instancesInfo.getNumClasses().intValue(), instances.numClasses());
        assertEquals(instancesInfo.getNumAttributes().intValue(), instances.numAttributes());
        assertEquals(instancesInfo.getNumInstances().intValue(), instances.numInstances());
        assertEquals(instancesInfo.getRelationName(), instances.relationName());

        assertNotNull(evaluationLog.getInputOptionsList());
        assertEquals(evaluationLog.getInputOptionsList().size(), 3L);

    }
}
