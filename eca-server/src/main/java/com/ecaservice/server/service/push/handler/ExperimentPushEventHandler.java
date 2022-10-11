package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatusVisitor;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.ERROR_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.FINISHED_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.IN_PROGRESS_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.NEW_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.TIMEOUT_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.EXPERIMENT;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_REQUEST_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_REQUEST_STATUS_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_STATUS_MESSAGE_TYPE;

/**
 * Experiment web push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentPushEventHandler extends AbstractSystemPushEventHandler<ExperimentWebPushEvent> {

    private final MessageTemplateProcessor messageTemplateProcessor;

    /**
     * Constructor with parameters.
     *
     * @param messageTemplateProcessor - message template processor
     */
    public ExperimentPushEventHandler(MessageTemplateProcessor messageTemplateProcessor) {
        super(ExperimentWebPushEvent.class);
        this.messageTemplateProcessor = messageTemplateProcessor;
    }

    @Override
    protected String getMessageType() {
        return EXPERIMENT_STATUS_MESSAGE_TYPE;
    }

    @Override
    protected String getMessageText(ExperimentWebPushEvent experimentWebPushEvent) {
        var experiment = experimentWebPushEvent.getExperiment();
        return experiment.getRequestStatus().handle(new RequestStatusVisitor<>() {
            @Override
            public String caseNew(Experiment exp) {
                return messageTemplateProcessor.process(NEW_EXPERIMENT, Collections.singletonMap(EXPERIMENT, exp));
            }

            @Override
            public String caseFinished(Experiment exp) {
                return messageTemplateProcessor.process(FINISHED_EXPERIMENT, Collections.singletonMap(EXPERIMENT, exp));
            }

            @Override
            public String caseTimeout(Experiment exp) {
                return messageTemplateProcessor.process(TIMEOUT_EXPERIMENT, Collections.singletonMap(EXPERIMENT, exp));
            }

            @Override
            public String caseError(Experiment exp) {
                return messageTemplateProcessor.process(ERROR_EXPERIMENT, Collections.singletonMap(EXPERIMENT, exp));
            }

            @Override
            public String caseInProgress(Experiment exp) {
                return messageTemplateProcessor.process(IN_PROGRESS_EXPERIMENT,
                        Collections.singletonMap(EXPERIMENT, exp));
            }
        }, experiment);
    }

    @Override
    protected Map<String, String> createAdditionalProperties(ExperimentWebPushEvent experimentWebPushEvent) {
        var experiment = experimentWebPushEvent.getExperiment();
        return Map.of(
                EXPERIMENT_ID_PROPERTY, String.valueOf(experiment.getId()),
                EXPERIMENT_REQUEST_ID_PROPERTY, experiment.getRequestId(),
                EXPERIMENT_REQUEST_STATUS_PROPERTY, experiment.getRequestStatus().name()
        );
    }
}
