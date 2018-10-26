package com.ecaservice.mapping;

import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.InstancesInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.HashMap;
import java.util.Map;

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
        if (evaluationRequest.getClassifier() != null) {
            evaluationLog.setClassifierName(evaluationRequest.getClassifier().getClass().getSimpleName());
            String[] options = evaluationRequest.getClassifier().getOptions();
            Map<String, String> optionsMap = new HashMap<>();
            for (int i = 0; i < options.length; i += 2) {
                optionsMap.put(options[i], options[i + 1]);
            }
            evaluationLog.setInputOptionsMap(optionsMap);
        }
    }

    @AfterMapping
    protected void mapData(EvaluationRequest evaluationRequest, @MappingTarget EvaluationLog evaluationLog) {
        if (evaluationRequest.getData() != null) {
            InstancesInfo instancesInfo = new InstancesInfo();
            instancesInfo.setRelationName(evaluationRequest.getData().relationName());
            instancesInfo.setNumInstances(evaluationRequest.getData().numInstances());
            instancesInfo.setNumAttributes(evaluationRequest.getData().numAttributes());
            instancesInfo.setNumClasses(evaluationRequest.getData().numClasses());
            instancesInfo.setClassName(evaluationRequest.getData().classAttribute().name());
            evaluationLog.setInstancesInfo(instancesInfo);
        }
    }
}
