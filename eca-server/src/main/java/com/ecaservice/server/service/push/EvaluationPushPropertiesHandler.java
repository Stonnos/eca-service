package com.ecaservice.server.service.push;

import com.ecaservice.server.event.model.push.PushMessageParams;
import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.service.push.dictionary.EvaluationPushProperty;
import com.ecaservice.server.service.push.dictionary.EvaluationPushPropertyVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Evaluation push properties handler.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class EvaluationPushPropertiesHandler {

    private final EvaluationPushPropertyVisitor evaluationPushPropertyVisitor;

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
