package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
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
     * @param instancesRequest - instances request
     * @return classifier options request
     */
    @Mapping(source = "evaluationMethod", target = "evaluationMethodReport.evaluationMethod")
    @Mapping(source = "numFolds", target = "evaluationMethodReport.numFolds")
    @Mapping(source = "numTests", target = "evaluationMethodReport.numTests")
    @Mapping(source = "seed", target = "evaluationMethodReport.seed")
    ClassifierOptionsRequest map(InstancesRequestDataModel instancesRequest);
}
