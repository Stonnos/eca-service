package com.ecaservice.mapping.mapstruct;

import com.ecaservice.model.InputData;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.evaluation.EvaluationRequest;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman Batygin
 */
//@Mapper
public abstract class EvaluationLogMapper {

    @Mappings({
            @Mapping(source = "ipAddress", target = "ipAddress"),
            @Mapping(source = "evaluationMethod", target = "evaluationMethod"),
            @Mapping(source = "evaluationOptionsMap", target = "evaluationOptionsMap"),
            @Mapping(target = "classifierName", ignore = true),
            @Mapping(target = "inputOptionsMap", ignore = true)
    })
    public abstract EvaluationLog map(EvaluationRequest evaluationRequest);

    private void mapClassifier(EvaluationRequest evaluationRequest, @MappingTarget EvaluationLog evaluationLog) {
        InputData inputData = evaluationRequest.getInputData();
        evaluationLog.setClassifierName(inputData.getClassifier().getClass().getSimpleName());
        String[] options = inputData.getClassifier().getOptions();
        Map<String, String> optionsMap = new HashMap<>();
        for (int i = 0; i < options.length; i += 2) {
            optionsMap.put(options[i], options[i + 1]);
        }
        evaluationLog.setInputOptionsMap(optionsMap);
    }
}
