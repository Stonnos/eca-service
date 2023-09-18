package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.PushMessageParams;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import com.ecaservice.server.service.push.dictionary.ExperimentPushProperty;
import com.ecaservice.server.service.push.dictionary.ExperimentPushPropertyVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Experiment push message handler.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class ExperimentPushMessageHandler {

    private final MessageTemplateProcessor messageTemplateProcessor;
    private final ExperimentPushPropertyVisitor experimentPushPropertyVisitor;

    /**
     * Processes push message text.
     *
     * @param pushMessageParams - push message params
     * @param experiment        - experiment entity
     * @return message text
     */
    public String processMessageText(PushMessageParams pushMessageParams, Experiment experiment) {
        String templateContextVariable = pushMessageParams.getTemplateContextVariable();
        Map<String, Object> variables =
                Collections.singletonMap(templateContextVariable, experiment);
        return messageTemplateProcessor.process(pushMessageParams.getTemplateCode(), variables);
    }

    /**
     * Processes push message additional properties.
     *
     * @param pushMessageParams - push message params
     * @param experiment        - experiment entity
     * @return push message additional properties map
     */
    public Map<String, String> processAdditionalProperties(PushMessageParams pushMessageParams, Experiment experiment) {
        Map<String, String> variables = newHashMap();
        pushMessageParams.getMessageProperties().forEach(property -> {
            ExperimentPushProperty experimentPushProperty = ExperimentPushProperty.valueOf(property);
            String value = experimentPushProperty.visit(experimentPushPropertyVisitor, experiment);
            variables.put(experimentPushProperty.getPropertyName(), value);
        });
        return variables;
    }
}
