package com.ecaservice.auto.test.event.model;

import com.ecaservice.auto.test.entity.autotest.EmailTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.model.EmailMessage;
import lombok.Getter;

/**
 * Email test step event.
 *
 * @author Roman Batygin
 */
@Getter
public class EmailTestStepEvent extends AbstractTestStepEvent {

    private final EmailMessage emailMessage;
    private final EmailTestStepEntity emailTestStepEntity;
    private final ExperimentRequestEntity experimentRequestEntity;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source                  the object on which the event initially occurred or with
     *                                which the event is associated (never {@code null})
     * @param emailMessage            - email message
     * @param emailTestStepEntity     - email test step entity
     * @param experimentRequestEntity - experiment request entity
     */
    public EmailTestStepEvent(Object source,
                              EmailMessage emailMessage,
                              EmailTestStepEntity emailTestStepEntity,
                              ExperimentRequestEntity experimentRequestEntity) {
        super(source);
        this.emailMessage = emailMessage;
        this.emailTestStepEntity = emailTestStepEntity;
        this.experimentRequestEntity = experimentRequestEntity;
    }
}
