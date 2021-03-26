package com.ecaservice.mapping;

import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Implements mapping to classifier options request.
 *
 * @author Roman Batygin
 */
@Mapper(uses = InstancesConverter.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifierOptionsRequestMapper {

    /**
     * Maps specified params to classifier options request.
     *
     * @param instancesRequest      - instances request
     * @param crossValidationConfig - cross validation config
     * @return classifier options request
     */
    @Mapping(source = "instancesRequest.data", target = "instances", qualifiedByName = "instancesToInstancesReport")
    @Mapping(target = "evaluationMethodReport.evaluationMethod", constant = "CROSS_VALIDATION")
    @Mapping(source = "crossValidationConfig.numFolds", target = "evaluationMethodReport.numFolds")
    @Mapping(source = "crossValidationConfig.numTests", target = "evaluationMethodReport.numTests")
    @Mapping(source = "crossValidationConfig.seed", target = "evaluationMethodReport.seed")
    ClassifierOptionsRequest map(InstancesRequest instancesRequest, CrossValidationConfig crossValidationConfig);
}
