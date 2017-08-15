package com.ecaservice.mapping;

import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.InputOptionsList;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationStatus;
import com.ecaservice.model.entity.InstancesInfo;
import eca.model.ClassifierDescriptor;
import eca.core.evaluation.Evaluation;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;

/**
 * Implements the conversion of classification results into the evaluation log entity.
 *
 * @author Roman Batygin
 */
@Component
public class ClassificationResultToEvaluationLogConverter extends CustomConverter<ClassificationResult, EvaluationLog> {

    @Override
    public EvaluationLog convert(ClassificationResult result, Type<? extends EvaluationLog> logType) {
        EvaluationLog evaluationLog = new EvaluationLog();

        ClassifierDescriptor classifierDescriptor = result.getClassifierDescriptor();
        Classifier classifier = classifierDescriptor.getClassifier();
        Evaluation evaluation = classifierDescriptor.getEvaluation();

        evaluationLog.setClassifierName(classifier.getClass().getSimpleName());

        evaluationLog.setInputOptionsList(mapperFacade.map(classifier, InputOptionsList.class).getInputOptionsList());

        evaluationLog.setInstancesInfo(mapperFacade.map(evaluation.getData(), InstancesInfo.class));

        evaluationLog.setEvaluationStatus(result.isSuccess() ? EvaluationStatus.SUCCESS : EvaluationStatus.ERROR);
        evaluationLog.setErrorMessage(result.getErrorMessage());

        return evaluationLog;
    }
}
