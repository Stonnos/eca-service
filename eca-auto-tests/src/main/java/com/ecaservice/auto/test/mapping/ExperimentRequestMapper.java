package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.ExperimentRequestDto;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Experiment request mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentRequestMapper
        extends BaseEvaluationRequestMapper<ExperimentRequestEntity, ExperimentRequestDto> {

    public ExperimentRequestMapper() {
        super(ExperimentRequestEntity.class);
    }

    /**
     * Maps experiment request entity to dto model.
     *
     * @param experimentRequestEntity - experiment request entity
     * @return experiment request dto
     */
    @Mapping(source = "evaluationMethod.description", target = "evaluationMethodDescription")
    @Mapping(source = "experimentType.description", target = "experimentTypeDescription")
    @Mapping(target = "testSteps", ignore = true)
    public abstract ExperimentRequestDto map(ExperimentRequestEntity experimentRequestEntity);
}
