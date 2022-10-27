package com.ecaservice.server.service.experiment.step;

import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractExperimentStepHandler {

    @Getter
    private final ExperimentStep step;

    public abstract void handle(ExperimentContext experimentContext,
                                ExperimentStepEntity experimentStepEntity);
}
