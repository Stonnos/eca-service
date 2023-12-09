package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Implements mapping classifier options request to classifier options request entity.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {DateTimeConverter.class, InstancesInfoMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifierOptionsRequestModelMapper {

    /**
     * Maps classifier options request to classifier options request entity.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options request entity
     */
    @Mapping(source = "evaluationMethodReport.evaluationMethod", target = "evaluationMethod")
    @Mapping(source = "evaluationMethodReport.numFolds", target = "numFolds")
    @Mapping(source = "evaluationMethodReport.numTests", target = "numTests")
    @Mapping(source = "evaluationMethodReport.seed", target = "seed")
    ClassifierOptionsRequestModel map(ClassifierOptionsRequest classifierOptionsRequest);
}
