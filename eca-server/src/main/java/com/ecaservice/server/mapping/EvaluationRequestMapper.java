package com.ecaservice.server.mapping;

import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Evaluation request mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class EvaluationRequestMapper {

    /**
     * Maps to evaluation request.
     *
     * @param instancesRequestDataModel - classifier options request
     * @param crossValidationConfig - cross validation config
     * @return evaluation request
     */
    @Mapping(target = "evaluationMethod", constant = "CROSS_VALIDATION")
    @Mapping(source = "crossValidationConfig.numFolds", target = "numFolds")
    @Mapping(source = "crossValidationConfig.numTests", target = "numTests")
    @Mapping(source = "crossValidationConfig.seed", target = "seed")
    public abstract EvaluationRequestDataModel map(InstancesRequestDataModel instancesRequestDataModel,
                                                   CrossValidationConfig crossValidationConfig);
}
