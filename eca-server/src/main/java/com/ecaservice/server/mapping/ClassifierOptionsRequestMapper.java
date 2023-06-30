package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Implements mapping to classifier options request.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifierOptionsRequestMapper {

    /**
     * Maps specified params to classifier options request.
     *
     * @param instancesRequest      - instances request
     * @param crossValidationConfig - cross validation config
     * @return classifier options request
     */
    @Mapping(source = "instancesRequest.requestId", target = "requestId")
    @Mapping(target = "evaluationMethodReport.evaluationMethod", constant = "CROSS_VALIDATION")
    @Mapping(source = "crossValidationConfig.numFolds", target = "evaluationMethodReport.numFolds")
    @Mapping(source = "crossValidationConfig.numTests", target = "evaluationMethodReport.numTests")
    @Mapping(source = "crossValidationConfig.seed", target = "evaluationMethodReport.seed")
    @Mapping(source = "instancesRequest.dataMd5Hash", target = "dataHash")
    ClassifierOptionsRequest map(InstancesRequestDataModel instancesRequest,
                                 CrossValidationConfig crossValidationConfig);
}
