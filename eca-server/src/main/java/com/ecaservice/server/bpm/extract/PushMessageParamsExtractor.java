package com.ecaservice.server.bpm.extract;

import com.ecaservice.server.event.model.push.PushMessageParams;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ecaservice.server.bpm.CamundaVariables.PUSH_MESSAGE_PROPERTIES;
import static com.ecaservice.server.bpm.CamundaVariables.PUSH_MESSAGE_TYPE;
import static com.ecaservice.server.bpm.CamundaVariables.PUSH_TEMPLATE_CODE;
import static com.ecaservice.server.bpm.CamundaVariables.PUSH_TEMPLATE_CONTEXT_VARIABLE;
import static com.ecaservice.server.util.CamundaUtils.getValuesAsArray;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Camunda helper.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class PushMessageParamsExtractor {

    /**
     * Extracts push message params from execution.
     *
     * @param execution - delegate execution
     * @return push message params
     */
    public PushMessageParams extractPushMessageParams(DelegateExecution execution) {
        String messageType = getVariable(execution, PUSH_MESSAGE_TYPE, String.class);
        String templateCode = getVariable(execution, PUSH_TEMPLATE_CODE, String.class);
        String messageTemplateContextVariable = getVariable(execution, PUSH_TEMPLATE_CONTEXT_VARIABLE, String.class);
        List<String> messageProperties = getValuesAsArray(execution, PUSH_MESSAGE_PROPERTIES);
        log.debug("Push message type [{}], template code [{}], properties [{}] for execution [{}]", messageType,
                templateCode, messageProperties, execution.getProcessBusinessKey());
        return new PushMessageParams(messageType, templateCode, messageTemplateContextVariable, messageProperties);
    }
}
