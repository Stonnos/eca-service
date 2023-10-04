package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.PushMessageParams;
import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import com.ecaservice.server.service.push.dictionary.EvaluationPushProperty;
import com.ecaservice.server.service.push.dictionary.EvaluationPushPropertyVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Evaluation push message handler.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class EvaluationPushMessageHandler {

    private final MessageTemplateProcessor messageTemplateProcessor;
    private final EvaluationPushPropertyVisitor evaluationPushPropertyVisitor;

    /**
     * Processes push message text.
     *
     * @param pushMessageParams - push message params
     * @param evaluationEntity  - evaluation entity
     * @return message text
     */
    public String processMessageText(PushMessageParams pushMessageParams, AbstractEvaluationEntity evaluationEntity) {
        String templateContextVariable = pushMessageParams.getTemplateContextVariable();
        Map<String, Object> variables =
                Collections.singletonMap(templateContextVariable, evaluationEntity);
        return messageTemplateProcessor.process(pushMessageParams.getTemplateCode(), variables);
    }

    /**
     * Processes push message additional properties.
     *
     * @param pushMessageParams - push message params
     * @param evaluationEntity  - evaluation entity
     * @return push message additional properties map
     */
    public Map<String, String> processAdditionalProperties(PushMessageParams pushMessageParams,
                                                           AbstractEvaluationEntity evaluationEntity) {
        Map<String, String> variables = newHashMap();
        pushMessageParams.getMessageProperties().forEach(property -> {
            EvaluationPushProperty evaluationPushProperty = EvaluationPushProperty.valueOf(property);
            String value = evaluationPushProperty.visit(evaluationPushPropertyVisitor, evaluationEntity);
            variables.put(evaluationPushProperty.getPropertyName(), value);
        });
        return variables;
    }
}
