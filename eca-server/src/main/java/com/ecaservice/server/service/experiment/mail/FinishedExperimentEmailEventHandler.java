package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.server.event.model.mail.FinishedExperimentEmailEvent;
import com.ecaservice.server.service.experiment.dictionary.TemplateVariablesDictionary;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Finished experiment email event handler.
 *
 * @author Roman Batygin
 */
@Component
public class FinishedExperimentEmailEventHandler
        extends AbstractExperimentEmailEventHandler<FinishedExperimentEmailEvent> {

    /**
     * Constructor without parameters.
     */
    public FinishedExperimentEmailEventHandler() {
        super(FinishedExperimentEmailEvent.class, Templates.FINISHED_EXPERIMENT);
    }


    @Override
    public Map<String, String> createVariables(FinishedExperimentEmailEvent event) {
        var variablesMap = super.createVariables(event);
        variablesMap.put(TemplateVariablesDictionary.DOWNLOAD_URL_KEY,
                event.getExperiment().getExperimentDownloadUrl());
        return variablesMap;
    }
}
