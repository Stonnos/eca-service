package com.ecaservice.mapping;

import com.ecaservice.model.InputData;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.model.evaluation.EvaluationRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implements evaluation request to evaluation log mapping.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class EvaluationLogMapper {

    /**
     * Maps evaluation request to evaluation log.
     *
     * @param evaluationRequest evaluation request
     * @return evaluation log entity
     */
    public abstract EvaluationLog map(EvaluationRequest evaluationRequest);

    @AfterMapping
    protected void mapClassifier(EvaluationRequest evaluationRequest, @MappingTarget EvaluationLog evaluationLog) {
        InputData inputData = evaluationRequest.getInputData();
        if (Optional.ofNullable(inputData).map(InputData::getClassifier).isPresent()) {
            evaluationLog.setClassifierName(inputData.getClassifier().getClass().getSimpleName());
            String[] options = inputData.getClassifier().getOptions();
            Map<String, String> optionsMap = new HashMap<>();
            for (int i = 0; i < options.length; i += 2) {
                optionsMap.put(options[i], options[i + 1]);
            }
            evaluationLog.setInputOptionsMap(optionsMap);
        }
    }

    @AfterMapping
    protected void mapData(EvaluationRequest evaluationRequest, @MappingTarget EvaluationLog evaluationLog) {
        InputData inputData = evaluationRequest.getInputData();
        if (Optional.ofNullable(inputData).map(InputData::getData).isPresent()) {
            InstancesInfo instancesInfo = new InstancesInfo();
            instancesInfo.setRelationName(inputData.getData().relationName());
            instancesInfo.setNumInstances(inputData.getData().numInstances());
            instancesInfo.setNumAttributes(inputData.getData().numAttributes());
            instancesInfo.setNumClasses(inputData.getData().numClasses());
            instancesInfo.setClassName(inputData.getData().classAttribute().name());
            evaluationLog.setInstancesInfo(instancesInfo);
        }
    }
}
