package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.server.event.model.mail.ErrorExperimentEmailEvent;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.springframework.stereotype.Component;

/**
 * Error experiment email event handler.
 *
 * @author Roman Batygin
 */
@Component
public class ErrorExperimentEmailEventHandler extends AbstractExperimentEmailEventHandler<ErrorExperimentEmailEvent> {

    /**
     * Constructor without parameters.
     */
    public ErrorExperimentEmailEventHandler() {
        super(ErrorExperimentEmailEvent.class, Templates.ERROR_EXPERIMENT);
    }
}
