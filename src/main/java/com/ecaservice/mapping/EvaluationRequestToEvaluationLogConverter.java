package com.ecaservice.mapping;

import com.ecaservice.model.EvaluationRequest;
import com.ecaservice.model.InputData;
import com.ecaservice.model.InputOptionsList;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationOptions;
import com.ecaservice.model.entity.InstancesInfo;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

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
        evaluationLog.setInputOptionsList(mapperFacade.map(inputData.getClassifier(), InputOptionsList.class)
                .getInputOptionsList());
        evaluationLog.setInstancesInfo(mapperFacade.map(inputData.getData(), InstancesInfo.class));
        evaluationLog.setEvaluationMethod(request.getEvaluationMethod());
        evaluationLog.setEvaluationOptions(new EvaluationOptions(request.getNumFolds(), request.getNumTests()));
        return evaluationLog;
    }
}
