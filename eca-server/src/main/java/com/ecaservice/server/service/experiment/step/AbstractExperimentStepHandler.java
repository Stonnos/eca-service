package com.ecaservice.server.service.experiment.step;

import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Abstract experiment step handler.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractExperimentStepHandler {

    @Getter
    private final ExperimentStep step;

    /**
     * Handles experiment step.
     *
     * @param experimentContext    - experiment context
     * @param experimentStepEntity - experiment step entity
     */
    public abstract void handle(ExperimentContext experimentContext,
                                ExperimentStepEntity experimentStepEntity);
}
