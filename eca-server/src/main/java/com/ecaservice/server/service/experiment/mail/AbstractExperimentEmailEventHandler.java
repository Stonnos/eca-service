package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.core.mail.client.event.listener.handler.AbstractEmailEventHandler;
import com.ecaservice.server.event.model.mail.AbstractExperimentEmailEvent;
import com.ecaservice.server.service.experiment.dictionary.TemplateVariablesDictionary;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Abstract experiment email event handler.
 *
 * @param <T> - experiment email event generic type
 * @author Roman Batygin
 */
public abstract class AbstractExperimentEmailEventHandler<T extends AbstractExperimentEmailEvent>
        extends AbstractEmailEventHandler<T> {

    protected AbstractExperimentEmailEventHandler(Class<T> type, String templateCode) {
        super(type, templateCode);
    }

    @Override
    public String getReceiver(AbstractExperimentEmailEvent event) {
        return event.getExperiment().getEmail();
    }

    @Override
    public Map<String, String> createVariables(AbstractExperimentEmailEvent event) {
        var experiment = event.getExperiment();
        Map<String, String> variablesMap = newHashMap();
        variablesMap.put(TemplateVariablesDictionary.EXPERIMENT_TYPE_KEY,
                experiment.getExperimentType().getDescription());
        variablesMap.put(TemplateVariablesDictionary.REQUEST_ID_KEY, experiment.getRequestId());
        return variablesMap;
    }
}
