package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.EvaluationResultsTestStepDto;
import com.ecaservice.auto.test.entity.autotest.EvaluationResultsTestStepEntity;
import org.mapstruct.Mapper;

/**
 * Evaluation results test step mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class EvaluationResultsTestStepMapper
        extends BaseTestStepMapper<EvaluationResultsTestStepEntity, EvaluationResultsTestStepDto> {

    protected EvaluationResultsTestStepMapper() {
        super(EvaluationResultsTestStepEntity.class);
    }
}
