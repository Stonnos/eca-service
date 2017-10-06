package com.ecaservice.mapping;

import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.model.InputData;
import com.ecaservice.model.InputOptionsMap;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationMethod;
import com.ecaservice.model.entity.InstancesInfo;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;

import static com.ecaservice.dictionary.EcaServiceDictionary.NUMBER_OF_FOLDS_KEY;
import static com.ecaservice.dictionary.EcaServiceDictionary.NUMBER_OF_TESTS_KEY;

/**
 * Implements the conversion of evaluation request into the evaluation log entity.
 *
 * @author Roman Batygin
 */
@Component
public class EvaluationRequestToEvaluationLogConverter extends CustomConverter<EvaluationRequest, EvaluationLog> {

    @Override
    public EvaluationLog convert(EvaluationRequest request, Type<? extends EvaluationLog> logType) {
        EvaluationLog evaluationLog = new EvaluationLog();
        evaluationLog.setIpAddress(request.getIpAddress());
        evaluationLog.setRequestDate(request.getRequestDate());
        InputData inputData = request.getInputData();
        evaluationLog.setClassifierName(inputData.getClassifier().getClass().getSimpleName());
        evaluationLog.setInputOptionsMap(mapperFacade.map(inputData.getClassifier(), InputOptionsMap.class)
                .getInputOptionsMap());
        evaluationLog.setInstancesInfo(mapperFacade.map(inputData.getData(), InstancesInfo.class));
        evaluationLog.setEvaluationMethod(request.getEvaluationMethod());

        if (EvaluationMethod.CROSS_VALIDATION.equals(request.getEvaluationMethod())) {
            evaluationLog.setEvaluationOptionsMap(new HashMap<>());
            evaluationLog.getEvaluationOptionsMap().put(NUMBER_OF_FOLDS_KEY, String.valueOf(request.getNumFolds()));
            evaluationLog.getEvaluationOptionsMap().put(NUMBER_OF_TESTS_KEY, String.valueOf(request.getNumTests()));
        } else {
            evaluationLog.setEvaluationOptionsMap(Collections.emptyMap());
        }
        return evaluationLog;
    }
}
