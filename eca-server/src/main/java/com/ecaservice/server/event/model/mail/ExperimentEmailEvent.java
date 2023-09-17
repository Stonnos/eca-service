package com.ecaservice.server.event.model.mail;

import com.ecaservice.core.mail.client.event.model.AbstractEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.experiment.mail.ExperimentEmailTemplateVariable;
import lombok.Getter;

import java.util.List;

/**
 * Experiment email event.
 *
 * @author Roman Batygin
 */
public class ExperimentEmailEvent extends AbstractEmailEvent {

    /**
     * Experiment entity
     */
    @Getter
    private final Experiment experiment;
    /**
     * Message template code
     */
    @Getter
    private final String templateCode;
    /**
     * Template variables
     */
    @Getter
    private final List<ExperimentEmailTemplateVariable> templateVariables;

    /**
     * Create a new event.
     *
     * @param source            - the object on which the event initially occurred (never {@code null})
     * @param experiment        - experiment entity
     * @param templateCode      - template code
     * @param templateVariables - template variables
     */
    public ExperimentEmailEvent(Object source,
                                Experiment experiment,
                                String templateCode,
                                List<ExperimentEmailTemplateVariable> templateVariables) {
        super(source);
        this.experiment = experiment;
        this.templateCode = templateCode;
        this.templateVariables = templateVariables;
    }
}
