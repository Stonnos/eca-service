package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.server.event.model.mail.NewExperimentEmailEvent;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.springframework.stereotype.Component;

/**
 * New experiment email event handler.
 *
 * @author Roman Batygin
 */
@Component
public class NewExperimentEmailEventHandler extends AbstractExperimentEmailEventHandler<NewExperimentEmailEvent> {

    /**
     * Constructor without parameters.
     */
    public NewExperimentEmailEventHandler() {
        super(NewExperimentEmailEvent.class, Templates.NEW_EXPERIMENT);
    }
}
