package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.ExperimentResultsTestStepDto;
import com.ecaservice.auto.test.entity.autotest.ExperimentResultsTestStepEntity;
import org.mapstruct.Mapper;

/**
 * Experiment results test step mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentResultsTestStepMapper
        extends BaseTestStepMapper<ExperimentResultsTestStepEntity, ExperimentResultsTestStepDto> {

    public ExperimentResultsTestStepMapper() {
        super(ExperimentResultsTestStepEntity.class);
    }
}
