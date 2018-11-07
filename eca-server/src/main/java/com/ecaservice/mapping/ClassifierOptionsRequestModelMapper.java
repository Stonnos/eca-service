package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.web.dto.ClassifierOptionsRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Implements mapping classifier options request to classifier options request entity.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {ErsEvaluationMethodMapper.class, ClassifierOptionsResponseModelMapper.class})
public interface ClassifierOptionsRequestModelMapper {

    /**
     * Maps classifier options request to classifier options request entity.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options request entity
     */
    @Mappings( {
            @Mapping(source = "evaluationMethodReport.evaluationMethod", target = "evaluationMethod"),
            @Mapping(source = "evaluationMethodReport.numFolds", target = "numFolds"),
            @Mapping(source = "evaluationMethodReport.numTests", target = "numTests"),
            @Mapping(source = "evaluationMethodReport.seed", target = "seed"),
    })
    ClassifierOptionsRequestModel map(ClassifierOptionsRequest classifierOptionsRequest);

    /**
     * Maps classifier options request model entity to its dto model.
     *
     * @param classifierOptionsRequestModel - classifier options request model entity
     * @return classifier options request dto model
     */
    ClassifierOptionsRequestDto map(ClassifierOptionsRequestModel classifierOptionsRequestModel);
}
