package com.ecaservice.server.service.push;

import com.ecaservice.core.message.template.service.MessageTemplateProcessor;
import com.ecaservice.server.event.model.push.PushMessageParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Evaluation push message processor.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class PushMessageProcessor {

    private final MessageTemplateProcessor messageTemplateProcessor;

    /**
     * Processes push message text.
     *
     * @param pushMessageParams - push message params
     * @param evaluationEntity  - evaluation entity
     * @return message text
     */
    public String processMessageText(PushMessageParams pushMessageParams, Object evaluationEntity) {
        String templateContextVariable = pushMessageParams.getTemplateContextVariable();
        Map<String, Object> variables =
                Collections.singletonMap(templateContextVariable, evaluationEntity);
        return messageTemplateProcessor.process(pushMessageParams.getTemplateCode(), variables);
    }
}
