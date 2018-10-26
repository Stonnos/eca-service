package com.ecaservice.mapping;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Implements mapping to classifier options request.
 *
 * @author Roman Batygin
 */
@Mapper(uses = InstancesConverter.class)
public interface ClassifierOptionsRequestMapper {

    /**
     * Maps specified params to classifier options request.
     *
     * @param instancesRequest      - instances request
     * @param crossValidationConfig - cross validation config
     * @return classifier options request
     */
    @Mappings({
            @Mapping(source = "instancesRequest.data", target = "instances",
                    qualifiedByName = "instancesToInstancesReport"),
            @Mapping(target = "evaluationMethodReport.evaluationMethod", constant = "CROSS_VALIDATION"),
            @Mapping(source = "crossValidationConfig.numFolds", target = "evaluationMethodReport.numFolds"),
            @Mapping(source = "crossValidationConfig.numTests", target = "evaluationMethodReport.numTests"),
            @Mapping(source = "crossValidationConfig.seed", target = "evaluationMethodReport.seed")
    })
    ClassifierOptionsRequest map(InstancesRequest instancesRequest,
                                 CrossValidationConfig crossValidationConfig);
}
