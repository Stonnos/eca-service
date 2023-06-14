package com.ecaservice.server.service.experiment;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatusVisitor;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.ERROR_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.FINISHED_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.IN_PROGRESS_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.NEW_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.TIMEOUT_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.EXPERIMENT;

/**
 * Experiment message template processor.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class ExperimentMessageTemplateProcessor {

    private final MessageTemplateProcessor messageTemplateProcessor;

    /**
     * Processes experiment message template.
     *
     * @param experiment - experiment entity
     * @return result message
     */
    public String process(Experiment experiment) {
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
}
