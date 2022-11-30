package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.server.event.model.mail.InProgressExperimentEmailEvent;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.springframework.stereotype.Component;

/**
 * In progress experiment email event handler.
 *
 * @author Roman Batygin
 */
@Component
public class InProgressExperimentEmailEventHandler extends AbstractExperimentEmailEventHandler<InProgressExperimentEmailEvent> {

    /**
     * Constructor without parameters.
     */
    public InProgressExperimentEmailEventHandler() {
        super(InProgressExperimentEmailEvent.class, Templates.IN_PROGRESS_EXPERIMENT);
    }
}
