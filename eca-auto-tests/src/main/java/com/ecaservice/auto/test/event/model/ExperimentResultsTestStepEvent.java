package com.ecaservice.auto.test.event.model;

import com.ecaservice.auto.test.entity.autotest.ExperimentResultsTestStepEntity;
import lombok.Getter;

/**
 * Experiment results test step event.
 *
 * @author Roman Batygin
 */
@Getter
public class ExperimentResultsTestStepEvent extends AbstractTestStepEvent {

    private final ExperimentResultsTestStepEntity experimentResultsTestStepEntity;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source                          the object on which the event initially occurred or with
     *                                        which the event is associated (never {@code null})
     * @param experimentResultsTestStepEntity - experiment results test step entity
     */
    public ExperimentResultsTestStepEvent(Object source,
                                          ExperimentResultsTestStepEntity experimentResultsTestStepEntity) {
        super(source);
        this.experimentResultsTestStepEntity = experimentResultsTestStepEntity;
    }
}
